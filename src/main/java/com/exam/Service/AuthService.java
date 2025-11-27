package com.exam.Service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.Exception.GlobalExceptionHandler;
import com.exam.Repositry.AuthRepo;
import com.exam.Response.ApiResponses;
import com.exam.Response.ResponseBean;
import com.exam.Security.TokenService;
import com.exam.reqDTO.CommonReqModel;
import com.exam.resDTO.LoginResModel;

@Service
public class AuthService {
	
	
	@Autowired
	AuthRepo authrepo;
	@Autowired
	TokenService tokenservice;
	
	public ResponseEntity<ApiResponses> loginService(ResponseBean response, CommonReqModel model){
		List<Map<String,Object>> data=null;
		LoginResModel user=new LoginResModel();
		String token="";
		int tokenInsertres=0;
		try {
			if(model.getUuid().isEmpty() || model.getUuid().isBlank() || model.getUser_pwd().isBlank() || model.getUser_pwd().isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			data=authrepo.loginRepo(model.getUuid(), model.getUser_pwd());
			
			if(!data.isEmpty() && data!=null) {
				user.setUser_name(data.get(0).get("user_name").toString());
				user.setUser_mobile(data.get(0).get("user_mobile").toString());
				user.setUser_email(data.get(0).get("user_email").toString());
				user.setUser_branch(data.get(0).get("user_branch").toString());
				user.setUser_inst(data.get(0).get("user_inst").toString());
				token=tokenservice.generateToken(model.getUuid(), data.get(0).get("user_role").toString());
				tokenInsertres=authrepo.insertTokenRepo(model.getUuid(), token);
				if(tokenInsertres>0) {
					return response.AppResponse("LoginSuccess",token, user);
				}
				else {
					return response.AppResponse("Error",null, null);
				}
			}
			else {
				return response.AppResponse("Notfound",null, null); 
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		
	}
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<ApiResponses> refreshTokenService(ResponseBean response, String oldToken){
	
		String token="";
		int tokenInsertres=0,tokenUpdateres=0;
		try {
			if(oldToken.isBlank() || oldToken.isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			if(!tokenservice.validateTokenAndReturnBool(oldToken)) {
				throw new GlobalExceptionHandler.ExpiredException();
			}
				String[] tdata=tokenservice.decodeJWT(oldToken);
				String uuid=tdata[1];
				token=tokenservice.generateRefreshToken(oldToken);
//				System.out.println(token);
				if(token!=null) {
					tokenUpdateres=authrepo.updateTokenRecordRepo("Refresh", oldToken);
					tokenInsertres=authrepo.insertTokenRepo(uuid, token);
					System.out.println(tokenInsertres);
					System.out.println(tokenUpdateres+"--");
					if(tokenInsertres>0 && tokenUpdateres>0) {
						return response.AppResponse("RefreshSuccess",token, null);
					}
					else {
						return response.AppResponse("Error",null, null);
					}
				}
				else {
					return response.AppResponse("TokenValid",null, null);
				}
				
			
		}catch(Exception ex) {
			throw ex;
		}
		
	}
	
	
	public ResponseEntity<ApiResponses> logoutService(ResponseBean response, String oldToken){
	
		String token="";
		int tokenInsertres=0,tokenUpdateres=0;
		try {
			if(oldToken.isBlank() || oldToken.isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			if(!tokenservice.validateTokenAndReturnBool(oldToken)) {
				throw new GlobalExceptionHandler.ExpiredException();
			}
				
					tokenUpdateres=authrepo.updateTokenRecordRepo("Logout", oldToken);
					if( tokenUpdateres>0) {
						return response.AppResponse("LogoutSuccess",null, null);
					}
					else {
						return response.AppResponse("Error",null, null);
					}
				
			
		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		
	}
}
