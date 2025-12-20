package com.exam.Service;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.exam.Entity.InterviewFeedback;
import com.exam.Entity.MasSubscription;
import com.exam.Entity.MasUser;
import com.exam.Entity.UserProfile;
import com.exam.Entity.UserSubscription;
import com.exam.Exception.GlobalExceptionHandler;
import com.exam.Repositry.InterviewFeedbackRepository;
import com.exam.Repositry.MasSubscriptionRepository;
import com.exam.Repositry.UserProfileRepository;
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
	 UserProfileRepository userprofRepo;
	
	@Autowired
    UserSubscriptionRepository usersubRepo;
	
	@Autowired
    InterviewFeedbackRepository interviewFeedbackRepository;
	
	@Autowired
	MasSubscriptionRepository massubRepo;
	
	@Autowired
	 GeminiService geminiService;
	public ResponseEntity<ApiResponses> getQsListService(CommonReqModel model, ResponseBean response, String authToken){
		List<String> data=null,data2=null,data3=null;
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
				
				int userCount=subData.get().getTCount();
				
				String userSubType=subData.get().getSubType();
				
				
				Optional<MasSubscription> masSubData=massubRepo.findBySubType(userSubType);
				
				int subLimit=masSubData.get().getLimit();
				
				if(userCount<subLimit) {
					data=geminiService.askGeminiForQuestions(model.getLevel(), model.getDomain(),userSubType);
					if(data!=null) {
						
						if("S".equals(userSubType) && !"".equals(model.getResume()) && "".equals(model.getJob_description())){
//							System.out.println(1);
							data2=geminiService.askGeminiForResumeQuestions(model.getLevel(), model.getResume(),userSubType);
							
							for (String item : data) {
								data2.add(item);
							}
							return response.AppResponse("Success", null, data2);
						}
						if("G".equals(userSubType) && !"".equals(model.getResume())){
//							System.out.println(1);
							data2=geminiService.askGeminiForResumeQuestions(model.getLevel(), model.getResume(),userSubType);
							
							for (String item : data) {
								data2.add(item);
							}
						}
						if("G".equals(userSubType) && !"".equals(model.getJob_description())) {
//							System.out.println(2);
							data3=geminiService.askGeminiForJDQuestions(model.getLevel(), model.getJob_description(),userSubType);
						
//							
							for (String item : data2) {
								data3.add(item);
							}
							for (String item : data) {
								data3.add(item);
							}
							
							return response.AppResponse("Success", null, data3);
							
						}
						
						return response.AppResponse("Success", null, data);
					}
					else {
						return response.AppResponse("TryAgain", null, null);
					}
//					return response.AppResponse("Success", null, List.of("What is Spring boot?","What is JAva"));
					
				}
				else {
					return response.AppResponse("SubExp", null, null);
				}
				
				
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	
	public ResponseEntity<ApiResponses> getQsListRoleBasedService(CommonReqModel model, ResponseBean response, String authToken){
		List<String> data=null,data2=null,data3=null;
		try {
			if(authToken.isBlank() || authToken.isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			if(!tokenservice.validateTokenAndReturnBool(authToken)) {
				throw new GlobalExceptionHandler.ExpiredException();
			}
			
			if(model.getLevel()==null || model.getDomain()==null) {
				return response.AppResponse("Error", null, null);
			}
				String[] tdata=tokenservice.decodeJWT(authToken);
				String uuid=tdata[1];
				String role=tdata[0];
//				System.out.println(role);
				
				
				Optional<UserSubscription> subData=usersubRepo.findByUuid(uuid);
				
				int userCount=subData.get().getTCount();
				
				String userSubType=subData.get().getSubType();
				
				
				Optional<MasSubscription> masSubData=massubRepo.findBySubType(userSubType);
				
				int subLimit=masSubData.get().getLimit();
				
				if(userCount<subLimit) {
					data=geminiService.askGeminiForRoleBasedQuestions(model.getLevel(), model.getDomain());
					if(data!=null) {
						
						
						return response.AppResponse("Success", null, data);
					}
					else {
						return response.AppResponse("TryAgain", null, null);
					}
//					return response.AppResponse("Success", null, List.of("What is Spring boot?","What is JAva"));
					
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
//				data=geminiService.getDummyInterviewFeedback();
				if(data!=null) {
					
					
					 Map<String, Object> scores = (Map<String, Object>) data.get("scores");

					    InterviewFeedback fb = new InterviewFeedback();
					    fb.setType(model.getType());
					    fb.setTechnicalScore(Double.parseDouble(scores.get("technicalScore").toString()));
					    fb.setCommunicationScore(Double.parseDouble(scores.get("communicationScore").toString()));
					    fb.setVoiceClarityScore(Double.parseDouble(scores.get("voiceClarityScore").toString()));
					    fb.setOverallScore(Double.parseDouble(scores.get("overallScore").toString()));

					    fb.setStrengths((List<String>) data.get("strengths"));
					    fb.setImprovements((List<String>) data.get("improvements"));

					    fb.setVerdict((String) data.get("verdict"));
					    fb.setUuid(uuid);
					    fb.setEntryTs(Instant.now());
					    String formattedTopics = Arrays.stream(model.getTopic().split("\\|"))
					            .map(String::trim)
					            .collect(Collectors.joining(", "));

					    fb.setTopics(formattedTopics);
					    fb.setVerdict((String) data.get("verdict"));

					 
					    Optional<UserSubscription> userOpt = usersubRepo.findByUuid(uuid);

					    if (userOpt.isPresent()) {
					        UserSubscription user = userOpt.get();
					        double score = fb.getOverallScore();
					        int coinsToAdd = 0;

					        if (score > 6.5 && score < 8) { 
					            coinsToAdd = 2;
					        } else if (score >= 8) {
					            coinsToAdd = 5;
					        }

					        // 3. Update and save only if coins were earned
					        if (coinsToAdd > 0) {
					            user.setCoin(user.getCoin() + coinsToAdd);
					            usersubRepo.save(user);
					        }
					    } else {
					        // Log or handle the case where the UUID doesn't exist
					        System.out.println("User not found for UUID: " + uuid);
					    }
					    
					    interviewFeedbackRepository.save(fb);
					
					    
					
					UserSubscription userSub=usersubRepo.findByUuid(uuid).get();
					
					userSub.setCount(userSub.getCount()+1);
					userSub.setTCount(userSub.getTCount()+1);
					
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
	
	
	public ResponseEntity<ApiResponses> ResumeParseService(MultipartFile file, ResponseBean response, String authToken) throws Exception {
	    try {
	        // 1. Basic Token Validation
	        if (authToken == null || authToken.isBlank()) {
	            return response.AppResponse("Nulltype", null, null);
	        }
	        if (!tokenservice.validateTokenAndReturnBool(authToken)) {
	            throw new GlobalExceptionHandler.ExpiredException();
	        }

	        String uuid = tokenservice.decodeJWT(authToken)[1];
	        
	        Optional<UserSubscription> userSubscription=usersubRepo.findByUuid(uuid);
            
	        if(userSubscription.isPresent()) {
	        	UserSubscription userSubscriptionData=userSubscription.get();
	        	if(userSubscriptionData.getCoin()<1) {
	        		return response.AppResponse("Insufficient", null, null);
	        	}
	        	 userSubscriptionData.setCoin(userSubscriptionData.getCoin()-1);
	        	 usersubRepo.save(userSubscriptionData);
	        }
	       
            
            
	        
	        // 2. File Validation (5MB limit)
	        if (file == null || file.isEmpty()) {
	            return response.AppResponse("Error", null, "File is empty");
	        }
	        if (file.getSize() > 5 * 1024 * 1024) { // 5MB check
	            return response.AppResponse("Error",  null,"File size exceeds 5MB limit");
	        }

	     
	        Optional<UserSubscription> subData = usersubRepo.findByUuid(uuid);

	        if (subData.isEmpty()) {
	            return response.AppResponse("Unauothorize", null, null);
	        }

	        String userSubType = subData.get().getSubType();
	        // Fixed logic: assuming 'G' or 'B' are allowed types
	        if (!"G".equals(userSubType) && !"S".equals(userSubType)) {
	            return response.AppResponse("Unauothorize", null, null);
	        }

	        // 4. Text Extraction using Apache Tika
	        Tika tika = new Tika();
	        String extractedText;
	        try (InputStream stream = file.getInputStream()) {
	            extractedText = tika.parseToString(stream);
	        }

	        if (extractedText == null || extractedText.isBlank()) {
	            return response.AppResponse("Error", "Could not extract text from file", null);
	        }

	        Map<String, Object> analysisResult = geminiService.askGeminiForResumeAnalyze(extractedText);
	        
	        if (analysisResult != null) {
	            return response.AppResponse("Success", null, analysisResult);
	        }
	        
	        return response.AppResponse("Error", null, null);

	    } catch (Exception ex) {
	    	ex.printStackTrace();
	        throw ex;
	    }
	}
	
	public ResponseEntity<ApiResponses> ResumeJDParseService(MultipartFile file, String JD, ResponseBean response, String authToken) throws Exception {
	    try {
	        // 1. Basic Token Validation
	        if (authToken == null || authToken.isBlank()) {
	            return response.AppResponse("Nulltype", null, null);
	        }
	        if (!tokenservice.validateTokenAndReturnBool(authToken)) {
	            throw new GlobalExceptionHandler.ExpiredException();
	        }
	        
	        String uuid = tokenservice.decodeJWT(authToken)[1];
	        
	        Optional<UserSubscription> userSubscription=usersubRepo.findByUuid(uuid);
            
	        if(userSubscription.isPresent()) {
	        	UserSubscription userSubscriptionData=userSubscription.get();
	        	if(userSubscriptionData.getCoin()<1) {
	        		return response.AppResponse("Insufficient", null, null);
	        	}
	        	 userSubscriptionData.setCoin(userSubscriptionData.getCoin()-1);
	        	 usersubRepo.save(userSubscriptionData);
	        }
	        
	        // 2. File Validation (5MB limit)
	        if (file == null || file.isEmpty()) {
	            return response.AppResponse("Error", null, "File is empty");
	        }
	        if (file.getSize() > 5 * 1024 * 1024) { // 5MB check
	            return response.AppResponse("Error",  null,"File size exceeds 5MB limit");
	        }

	       
	        Optional<UserSubscription> subData = usersubRepo.findByUuid(uuid);

	        if (subData.isEmpty()) {
	            return response.AppResponse("Unauothorize", null, null);
	        }

	        String userSubType = subData.get().getSubType();
	        // Fixed logic: assuming 'G' or 'B' are allowed types
	        if (!"G".equals(userSubType) && !"S".equals(userSubType)) {
	            return response.AppResponse("Unauothorize", null, null);
	        }

	        // 4. Text Extraction using Apache Tika
	        Tika tika = new Tika();
	        String extractedText;
	        try (InputStream stream = file.getInputStream()) {
	            extractedText = tika.parseToString(stream);
	        }

	        if (extractedText == null || extractedText.isBlank()) {
	            return response.AppResponse("Error", "Could not extract text from file", null);
	        }

	        Map<String, Object> analysisResult = geminiService.getJDMatchInsights(extractedText,JD);
	        
	        if (analysisResult != null) {
	            return response.AppResponse("Success", null, analysisResult);
	        }
	        
	        return response.AppResponse("Error", null, null);

	    } catch (Exception ex) {
	    	ex.printStackTrace();
	        throw ex;
	    }
	}
	
	
	
	public ResponseEntity<ApiResponses> getAiRoadMapService(CommonReqModel model, ResponseBean response, String authToken){
		Map<String, Object> data=null;
		try {
			if(authToken.isBlank() || authToken.isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			if(!tokenservice.validateTokenAndReturnBool(authToken)) {
				throw new GlobalExceptionHandler.ExpiredException();
			}
			
			String uuid = tokenservice.decodeJWT(authToken)[1];
	        
	        Optional<UserSubscription> userSubscription=usersubRepo.findByUuid(uuid);
            
	        if(userSubscription.isPresent()) {
	        	UserSubscription userSubscriptionData=userSubscription.get();
	        	if(userSubscriptionData.getCoin()<1) {
	        		return response.AppResponse("Insufficient", null, null);
	        	}
	        	 userSubscriptionData.setCoin(userSubscriptionData.getCoin()-1);
	        	 usersubRepo.save(userSubscriptionData);
	        }
			
				data=geminiService.getAiRoadmap(model.getTopic(),model.getImprovements());
				if(data!=null) {
					return response.AppResponse("Success", null, data);
				}
				else {
					return response.AppResponse("TryAgain", null, null);
				}
		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	
}
