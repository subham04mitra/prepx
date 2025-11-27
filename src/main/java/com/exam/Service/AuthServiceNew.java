package com.exam.Service;


import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.Entity.MasUser;
import com.exam.Entity.MasUserToken;
import com.exam.Exception.GlobalExceptionHandler;
import com.exam.Repositry.MasUserRepository;
import com.exam.Repositry.MasUserTokenRepository;
import com.exam.Response.ApiResponses;
import com.exam.Response.ResponseBean;
import com.exam.Security.TokenService;
import com.exam.reqDTO.CommonReqModel;
import com.exam.resDTO.LoginResModel;

@Service
public class AuthServiceNew {

    @Autowired
    MasUserRepository userRepo;

    @Autowired
    MasUserTokenRepository tokenRepo;

    @Autowired
    TokenService tokenservice;

    // -------------------------------------------------------------------
    // LOGIN IMPLEMENTATION (MONGO)
    // -------------------------------------------------------------------
    public ResponseEntity<ApiResponses> loginService(ResponseBean response, CommonReqModel model) {

        try {
            if (model.getUuid().isEmpty() || model.getUser_pwd().isEmpty()) {
                return response.AppResponse("Nulltype", null, null);
            }

            Optional<MasUser> userOpt =
                    userRepo.findByUuidAndUserPwdAndActiveFlag(model.getUuid(), model.getUser_pwd(), "Y");

            if (userOpt.isEmpty()) {
                return response.AppResponse("Notfound", null, null);
            }

            MasUser userDoc = userOpt.get();

            LoginResModel user = new LoginResModel();
            user.setUser_name(userDoc.getUserName());
            user.setUser_mobile(userDoc.getUserMobile());
            user.setUser_email(userDoc.getUserEmail());
            user.setUser_branch(userDoc.getUserBranch());
            user.setUser_inst(userDoc.getUserInst());

            String token = tokenservice.generateToken(model.getUuid(), userDoc.getUserRole());

            // Save token in DB
            MasUserToken tok = new MasUserToken();
            tok.setUuid(model.getUuid());
            tok.setJwt(token);
            tok.setIsInvalid(false);
            tok.setIsLogout(false);
            tok.setEntryTs(Instant.now());
            tokenRepo.save(tok);

            return response.AppResponse("LoginSuccess", token, user);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    // -------------------------------------------------------------------
    // REFRESH TOKEN IMPLEMENTATION (MONGO)
    // -------------------------------------------------------------------
    @Transactional
    public ResponseEntity<ApiResponses> refreshTokenService(ResponseBean response, String oldToken) {

        try {
            if (oldToken.isEmpty()) {
                return response.AppResponse("Nulltype", null, null);
            }

            if (!tokenservice.validateTokenAndReturnBool(oldToken)) {
                throw new GlobalExceptionHandler.ExpiredException();
            }

            String[] tdata = tokenservice.decodeJWT(oldToken);
            String uuid = tdata[1];

            // Generate new token
            String newToken = tokenservice.generateRefreshToken(oldToken);

            if (newToken == null) {
                return response.AppResponse("TokenValid", null, null);
            }

            // Mark old token as invalid = true
            MasUserToken oldTok = tokenRepo.findByJwt(oldToken).get();
            if (oldTok != null) {
                oldTok.setIsInvalid(true);
                tokenRepo.save(oldTok);
            }

            // Insert new token
            MasUserToken newTok = new MasUserToken();
            newTok.setUuid(uuid);
            newTok.setJwt(newToken);
            newTok.setIsInvalid(false);
            newTok.setIsLogout(false);
            newTok.setEntryTs(Instant.now());
            tokenRepo.save(newTok);

            return response.AppResponse("RefreshSuccess", newToken, null);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    // -------------------------------------------------------------------
    // LOGOUT IMPLEMENTATION (MONGO)
    // -------------------------------------------------------------------
    public ResponseEntity<ApiResponses> logoutService(ResponseBean response, String oldToken) {

        try {
            if (oldToken.isEmpty()) {
                return response.AppResponse("Nulltype", null, null);
            }

            if (!tokenservice.validateTokenAndReturnBool(oldToken)) {
                throw new GlobalExceptionHandler.ExpiredException();
            }

            MasUserToken tok = tokenRepo.findByJwt(oldToken).get();

            if (tok != null) {
                tok.setIsLogout(true);
                tokenRepo.save(tok);
                return response.AppResponse("LogoutSuccess", null, null);
            }

            return response.AppResponse("Error", null, null);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
