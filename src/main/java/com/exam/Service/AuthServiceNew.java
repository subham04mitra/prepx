package com.exam.Service;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.Entity.InterviewFeedback;
import com.exam.Entity.MasSubscription;
import com.exam.Entity.MasUser;
import com.exam.Entity.MasUserToken;
import com.exam.Entity.UserFeedback;
import com.exam.Entity.UserSubscription;
import com.exam.Exception.GlobalExceptionHandler;
import com.exam.Repositry.InterviewFeedbackRepository;
import com.exam.Repositry.MasSubscriptionRepository;
import com.exam.Repositry.MasUserRepository;
import com.exam.Repositry.MasUserTokenRepository;
import com.exam.Repositry.UserFeedbackRepository;
import com.exam.Repositry.UserSubscriptionRepository;
import com.exam.Response.ApiResponses;
import com.exam.Response.ResponseBean;
import com.exam.Security.TokenService;
import com.exam.reqDTO.CommonReqModel;
import com.exam.resDTO.LoginResModel;
import com.exam.resDTO.ProfileDTO;

@Service
public class AuthServiceNew {

    @Autowired
    MasUserRepository userRepo;
    
    @Autowired
    UserSubscriptionRepository usersubRepo;
    
    @Autowired
    UserFeedbackRepository userfeedbackRepo;

    @Autowired
    MasUserTokenRepository tokenRepo;
    
    @Autowired
    MasSubscriptionRepository massubRepo;

    @Autowired
    TokenService tokenservice;
    
    @Autowired
    InterviewFeedbackRepository interviewFeedbackRepository;

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
            user.setUser_branch(userDoc.getStream());
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
    
    
    
    public ResponseEntity<ApiResponses> registerService(ResponseBean response, CommonReqModel model) {

        try {
            if (model.getUuid().isEmpty() || model.getUser_pwd().isEmpty() || model.getEmail().isEmpty() || model.getInst().isEmpty()
            		|| model.getBranch().isEmpty() || model.getStream().isEmpty()) {
                return response.AppResponse("Nulltype", null, null);
            }
            

            MasUser userData =new MasUser();

            userData.setUserEmail(model.getEmail());
            userData.setUserMobile(model.getMobile());
            userData.setStream(model.getStream());
            userData.setUserBranch(model.getBranch());
            userData.setUserInst(model.getInst());
            userData.setUserName(model.getName());
            userData.setUserPwd(model.getUser_pwd());
            userData.setActiveFlag("Y");
            userData.setUserRole("ADMIN");
            userData.setEntryTs(Instant.now());
            userData.setUuid(model.getUuid());
            
            
            userRepo.save(userData);
            
            UserSubscription userSubscription=new UserSubscription();
            userSubscription.setUuid(model.getUuid());
            userSubscription.setSubType("F");
            userSubscription.setEntryTs(Instant.now());
            
            usersubRepo.save(userSubscription);
            
            
            return response.AppResponse("RegSuccess", null,null);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    
    public ResponseEntity<ApiResponses> checkuuidService(ResponseBean response, CommonReqModel model) {

        try {
            if (model.getUuid().isEmpty()) {
                return response.AppResponse("Nulltype", null, null);
            }
            
            
            Optional<MasUser> userData=userRepo.findByUuidAndActiveFlag(model.getUuid(), "Y");

           if(userData.isPresent()) {
        	   return response.AppResponse("Found", null,null);
           }
           else {
        	   return response.AppResponse("SuccessF", null,null);
           }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
 public ResponseEntity<ApiResponses> subscribeService(ResponseBean response, CommonReqModel model, String authToken) {

    try {
        if (authToken == null || authToken.isBlank()) {
            return response.AppResponse("Nulltype", null, null);
        }

        if (!tokenservice.validateTokenAndReturnBool(authToken)) {
            throw new GlobalExceptionHandler.ExpiredException();
        }

        String uuid = tokenservice.decodeJWT(authToken)[1];

        UserSubscription userSub = usersubRepo.findByUuid(uuid).get();
        MasSubscription currentSub = massubRepo.findBySubType(userSub.getSubType()).get();

        String newSubType = model.getType();
        MasSubscription requestedSub = massubRepo.findBySubType(newSubType).get();

        int userUsedCount = userSub.getTCount();
        int currentLimit = currentSub.getLimit();
        int newLimit = requestedSub.getLimit();

        boolean isExhausted = userUsedCount >= currentLimit;

        // ---------------- RULE 1: If user tries to downgrade ----------------
        if (!isUpgradeAllowed(userSub.getSubType(), newSubType)) {

            // downgrade allowed ONLY IF exhausted
            if (!isExhausted) {
                return response.AppResponse("DownGradeNotAllowed",
                        null,
                        currentSub.getSubName());
            }
        }

        // ---------------- RULE 2: Same plan purchase ----------------
        if (userSub.getSubType().equalsIgnoreCase(newSubType)) {

            // if still has remaining count → block
            if (!isExhausted) {
                return response.AppResponse("SubExists",
                        null,
                        currentSub.getSubName());
            }

            // exhausted → allow repurchase (reset count)
            userSub.setTCount(0);
            usersubRepo.save(userSub);

            return response.AppResponse("ReSubscribed",
                    null,
                    currentSub.getSubName());
        }

        // ---------------- RULE 3: Upgrade OR Exhausted Downgrade ----------------
        userSub.setSubType(newSubType);
        userSub.setTCount(0);
        usersubRepo.save(userSub);

        return response.AppResponse("ReSubscribed",
                null,
                requestedSub.getSubName());

    } catch (Exception ex) {
        ex.printStackTrace();
        throw ex;
    }
}

 
 
 public ResponseEntity<ApiResponses> saveFeedbackService(ResponseBean response, CommonReqModel model, String authToken) {

	    try {
	        if (authToken == null || authToken.isBlank()) {
	            return response.AppResponse("Nulltype", null, null);
	        }

	        if (!tokenservice.validateTokenAndReturnBool(authToken)) {
	            throw new GlobalExceptionHandler.ExpiredException();
	        }

	        String uuid = tokenservice.decodeJWT(authToken)[1];

	        
	        UserFeedback userFeedback=new UserFeedback();
	        
	        userFeedback.setUuid(uuid);
	        userFeedback.setRating(model.getRating());
	        userFeedback.setFeedback(model.getFeedback() != null ? model.getFeedback() : "");

	        userfeedbackRepo.save(userFeedback);
	        
	        return response.AppResponse("fSuccess",
	                null,
	                null);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        throw ex;
	    }
	}

 
 
 public ResponseEntity<ApiResponses> checksubscribeService(ResponseBean response, CommonReqModel model, String authToken) {

	    try {
	        if (authToken == null || authToken.isBlank()) {
	            return response.AppResponse("Nulltype", null, null);
	        }

	        if (!tokenservice.validateTokenAndReturnBool(authToken)) {
	            throw new GlobalExceptionHandler.ExpiredException();
	        }

	        String uuid = tokenservice.decodeJWT(authToken)[1];

	        UserSubscription userSub = usersubRepo.findByUuid(uuid).get();
	        MasSubscription currentSub = massubRepo.findBySubType(userSub.getSubType()).get();

	        String newSubType = model.getType();
	        MasSubscription requestedSub = massubRepo.findBySubType(newSubType).get();

	        int userUsedCount = userSub.getTCount();
	        int currentLimit = currentSub.getLimit();
	        int newLimit = requestedSub.getLimit();

	        boolean isExhausted = userUsedCount >= currentLimit;

	        // ---------------- RULE 1: If user tries to downgrade ----------------
	        if (!isUpgradeAllowed(userSub.getSubType(), newSubType)) {

	            // downgrade allowed ONLY IF exhausted
	            if (!isExhausted) {
	                return response.AppResponse("DownGradeNotAllowed",
	                        null,
	                        currentSub.getSubName());
	            }
	        }

	        // ---------------- RULE 2: Same plan purchase ----------------
	        if (userSub.getSubType().equalsIgnoreCase(newSubType)) {

	            // if still has remaining count → block
	            if (!isExhausted) {
	                return response.AppResponse("SubExists",
	                        null,
	                        currentSub.getSubName());
	            }

	            

	            return response.AppResponse("ReSubscribed",
	                    null,
	                    currentSub.getSubName());
	        }

	      

	        return response.AppResponse("ReSubscribed",
	                null,
	                requestedSub.getSubName());

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        throw ex;
	    }
	}

 
   private boolean isUpgradeAllowed(String current, String next) {

	    // Ordering: Basic < Silver < Gold
	    int rankCurrent = getRank(current);
	    int rankNext = getRank(next);
	    System.out.println(current+"--"+next);
	    System.out.println(rankCurrent+"--"+rankNext);
	    return rankNext >= rankCurrent;  // allow upgrade or same plan
	}

	private int getRank(String sub) {
	    return switch (sub.toLowerCase()) {
	        case "b" -> 1;
	        case "s" -> 2;
	        case "g" -> 3;
	        default -> 0;
	    };
	}

    
    
    public ResponseEntity<ApiResponses> profileService(ResponseBean response, String authToken) {

        try {
        	if(authToken.isBlank() || authToken.isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			if(!tokenservice.validateTokenAndReturnBool(authToken)) {
				throw new GlobalExceptionHandler.ExpiredException();
			}
            
            String[] tdata = tokenservice.decodeJWT(authToken);
            String uuid = tdata[1];
            MasUser userData=userRepo.findByUuidAndActiveFlag(uuid, "Y").get();
            UserSubscription usesubData =usersubRepo.findByUuid(uuid).get();
            MasSubscription masSub=massubRepo.findBySubType(usesubData.getSubType()).get();
            
            
            ProfileDTO profile=new ProfileDTO();
            
            profile.setName(userData.getUserName());
            profile.setMobile(userData.getUserMobile());
            profile.setEmail(userData.getUserEmail());
            profile.setInstitute(userData.getUserInst());
            profile.setStream(userData.getStream());
            profile.setCity(userData.getUserBranch());
            profile.setSubName(masSub.getSubName());
            profile.setIntCount(usesubData.getCount());
            profile.setTCount(masSub.getLimit()-usesubData.getTCount());
            profile.setCreationData(userData.getEntryTs());
            
          
            return response.AppResponse("Success", null,profile);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    
    public ResponseEntity<ApiResponses> viewRecordsService(ResponseBean response, String authToken) {

        try {
        	if(authToken.isBlank() || authToken.isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			if(!tokenservice.validateTokenAndReturnBool(authToken)) {
				throw new GlobalExceptionHandler.ExpiredException();
			}
            
            String[] tdata = tokenservice.decodeJWT(authToken);
            String uuid = tdata[1];
            
            
            
            List<InterviewFeedback> feedback=interviewFeedbackRepository.findByUuid(uuid);
          if(!feedback.isEmpty()) {
        	  return response.AppResponse("Success", null,feedback);
          }
          else {
        	  return response.AppResponse("Notfound", null,null);
          }
            
           

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    
}
