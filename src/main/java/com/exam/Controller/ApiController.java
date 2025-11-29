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
import com.exam.Service.AuthServiceNew;
import com.exam.reqDTO.CommonReqModel;

@RestController
@RequestMapping("/api/mas")
public class ApiController {

	@Autowired
	AuthServiceNew authserv;
	ResponseBean responseBean=new ResponseBean();
	
	@PostMapping("/subscribe")
	public ResponseEntity<ApiResponses> subscribeController(@RequestBody CommonReqModel model,@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.subscribeService(responseBean,model,authToken);
		
		return finalResponse;
}
	
	
	@GetMapping("/profile")
	public ResponseEntity<ApiResponses> profileController(@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.profileService(responseBean,authToken);
		
		return finalResponse;
}
	
	
	@GetMapping("/records")
	public ResponseEntity<ApiResponses> ViewRecordsController(@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.viewRecordsService(responseBean,authToken);
		
		return finalResponse;
}

}