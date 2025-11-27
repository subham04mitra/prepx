package com.exam.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.genai.types.GenerateContentResponse;

public class GetQuestionListGemini {

	
//	 public String askGemini(String prompt) {
//	        try {
//	            GenerateContentResponse response = geminiClient.models.generateContent(
//	                MODEL_NAME,
//	                prompt,
//	                null 
//	            );
//	           
//	            return response.text();
//	        } catch (Exception ex) {
//	            System.err.println("Gemini API Error: " + ex.getMessage());
//	            ex.printStackTrace();
//	            return "Error: Could not get response from Gemini API (" + ex.getMessage() + ")";
//	        }
//	    }
	    
	    
	
	public static List<String> GetQsList(String level,String[] domain){
		
		
		 List<String> data = Arrays.asList(
		    		"Tell me about Yourself?"
		    );
		 return data;
	}
	
	
	public static Map<String, Object> getInterviewFeedback(String text) {

	    Map<String, Object> response = new HashMap<>();

	    // --- Scores Section ---
	    Map<String, Object> scores = new HashMap<>();
	    scores.put("technicalScore", 7.5);
	    scores.put("communicationScore", 8.2);
	    scores.put("voiceClarityScore", 7.8);
	    scores.put("overallScore", 7.8);

	    // --- Strengths Section ---
	    List<String> strengths = Arrays.asList(
	            "Good understanding of core Java and Spring Boot fundamentals.",
	            "Confident communication and clear explanations.",
	            "Quick in structuring answers logically."
	    );

	    // --- Improvements Section ---
	    List<String> improvements = Arrays.asList(
	            "Work on deep-dive Spring Boot topics like filters, interceptors, actuator, and JPA internals.",
	            "Add more real-world examples when answering microservices questions.",
	            "Improve crispness in answering long questions."
	    );

	    // --- Final Verdict ---
	    String verdict = "You performed well overall. With a little polishing in advanced Spring Boot and microservices concepts, you can easily clear interviews for a Backend Developer role.";

	    // --- Question-wise Feedback ---
	    List<Map<String, String>> questionFeedback = new ArrayList<>();

	    Map<String, String> q1 = new HashMap<>();
	    q1.put("question", "Tell me about yourself");
	    q1.put("feedback", "Good introduction, but try to keep it under 1 minute and highlight key achievements first.");
	    
	    Map<String, String> q2 = new HashMap<>();
	    q2.put("question", "What is Spring Boot?");
	    q2.put("feedback", "Correct answer, but add points about auto-configuration and production-ready features.");

	    questionFeedback.add(q1);
	    questionFeedback.add(q2);
	    questionFeedback.add(q1);
	    questionFeedback.add(q2);
	    questionFeedback.add(q1);
	    questionFeedback.add(q2);
	    questionFeedback.add(q1);
	    questionFeedback.add(q2);
	    

	    // --- Build Final Response ---
	    response.put("scores", scores);
	    response.put("strengths", strengths);
	    response.put("improvements", improvements);
	    response.put("verdict", verdict);
	    response.put("questionFeedback", questionFeedback);

	    return response;
	}

	
}
