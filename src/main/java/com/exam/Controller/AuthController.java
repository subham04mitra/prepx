package com.exam.Controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.Response.ApiResponses;
import com.exam.Response.ResponseBean;
import com.exam.Service.AuthService;
import com.exam.Service.AuthServiceNew;
import com.exam.reqDTO.CommonReqModel;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthServiceNew authserv;
	ResponseBean responseBean=new ResponseBean();
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponses> loginController(@RequestBody CommonReqModel model){
		
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.loginService(responseBean,model);
		
		return finalResponse;	
	}
	
	
	@PostMapping("/google-login")
	public ResponseEntity<ApiResponses> googleloginController(@RequestBody CommonReqModel model) throws Exception{
		
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.googleLoginService(responseBean,model);
		
		return finalResponse;	
	}
	
	
	@PostMapping("/send-otp")
	public ResponseEntity<ApiResponses> otpController(@RequestBody CommonReqModel model) throws Exception{
		
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.otpService(responseBean,model);
		
		return finalResponse;
	}
	
	@PostMapping("/forget-password")
public ResponseEntity<ApiResponses> forgetPasswordController(@RequestBody CommonReqModel model) throws Exception{
	
	ResponseEntity<ApiResponses> finalResponse;
	
	finalResponse=authserv.forgetPasswordService(responseBean,model);
	
	return finalResponse;
}
	
	
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponses> refreshTokenController(@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.refreshTokenService(responseBean,authToken);
		
		return finalResponse;
	}
	
	@PostMapping("/logout")
	public ResponseEntity<ApiResponses> logoutController(@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.logoutService(responseBean,authToken);
		
		return finalResponse;
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponses> registrationController(@RequestBody CommonReqModel model){
		
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.registerService(responseBean,model);
		
		return finalResponse;
	}
	
	
	@PostMapping("/check-uuid")
	public ResponseEntity<ApiResponses> checkuuidController(@RequestBody CommonReqModel model){
		
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.checkuuidService(responseBean,model);
		
		return finalResponse;
	}

	
	
	
	
	
	

}
