package com.exam.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.exam.Exception.GlobalExceptionHandler;
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
				data=geminiService.askGeminiForQuestions(model.getLevel(), model.getDomain());
				if(!data.isEmpty()) {
					return response.AppResponse("Success", null, data);
				}
				else {
					return response.AppResponse("TryAgain", null, null);
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
