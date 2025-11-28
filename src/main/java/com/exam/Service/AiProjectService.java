package com.exam.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.exam.Entity.MasSubscription;
import com.exam.Entity.UserSubscription;
import com.exam.Exception.GlobalExceptionHandler;
import com.exam.Repositry.MasSubscriptionRepository;
import com.exam.Repositry.UserSubscriptionRepository;
import com.exam.Response.ApiResponses;
import com.exam.Response.ResponseBean;
import com.exam.Security.TokenService;
import com.exam.Util.GeminiService;
import com.exam.Util.GetQuestionListGemini;
import com.exam.reqDTO.CommonReqModel;
import com.exam.resDTO.MasResDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiProjectService {

	@Autowired
	TokenService tokenservice;
	
	@Autowired
    UserSubscriptionRepository usersubRepo;
	
	@Autowired
	MasSubscriptionRepository massubRepo;
	
	@Autowired
	 GeminiService geminiService;
	public ResponseEntity<ApiResponses> getQsListService(CommonReqModel model, ResponseBean response, String authToken){
		List<String> data=null;
		try {
			if(authToken.isBlank() || authToken.isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			if(!tokenservice.validateTokenAndReturnBool(authToken)) {
				throw new GlobalExceptionHandler.ExpiredException();
			}
			
			if(model.getLevel()==null || model.getDomain().length==0) {
				return response.AppResponse("Error", null, null);
			}
				String[] tdata=tokenservice.decodeJWT(authToken);
				String uuid=tdata[1];
				String role=tdata[0];
//				System.out.println(role);
				
				
				Optional<UserSubscription> subData=usersubRepo.findByUuid(uuid);
				
				int userCount=subData.get().getCount();
				
				String userSubType=subData.get().getSubType();
				
				
				Optional<MasSubscription> masSubData=massubRepo.findBySubType(userSubType);
				
				int subLimit=masSubData.get().getLimit();
				
				if(userCount<subLimit) {
					data=geminiService.askGeminiForQuestions(model.getLevel(), model.getDomain());
					if(!data.isEmpty()) {
						return response.AppResponse("Success", null, data);
					}
					else {
						return response.AppResponse("TryAgain", null, null);
					}
				}
				else {
					return response.AppResponse("SubExp", null, null);
				}
				
				
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	
	public ResponseEntity<ApiResponses> getFeedbackService(CommonReqModel model, ResponseBean response, String authToken){
		Map<String, Object> data=null;
		try {
			if(authToken.isBlank() || authToken.isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			if(!tokenservice.validateTokenAndReturnBool(authToken)) {
				throw new GlobalExceptionHandler.ExpiredException();
			}
			
			if(model.getRes()==null) {
				return response.AppResponse("Error", null, null);
			}
				String[] tdata=tokenservice.decodeJWT(authToken);
				String uuid=tdata[1];
				String role=tdata[0];
//				System.out.println(role);
				ObjectMapper mapper = new ObjectMapper();
				String jsonText = null;
				try {
					jsonText = mapper.writeValueAsString(model.getRes());
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
//				System.out.println(jsonText);
								
				data=geminiService.getInterviewFeedback(jsonText);
				if(!data.isEmpty()) {
					
					UserSubscription userSub=usersubRepo.findByUuid(uuid).get();
					
					userSub.setCount(userSub.getCount()+1);
					
					usersubRepo.save(userSub);
					
					
					
					return response.AppResponse("Success", null, data);
				}
				else {
					return response.AppResponse("TryAgain", null, null);
				}
		}catch(Exception ex) {
			throw ex;
		}
	}
	
}
