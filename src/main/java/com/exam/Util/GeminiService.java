package com.exam.Util;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
// Jackson Imports for JSON Deserialization
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class to interact with the Gemini API.
 * * STRATEGY: Prompt Engineering for JSON.
 * Instead of using the strict Schema object (which varies by SDK version), 
 * we instruct the model to return a specific JSON format and parse it manually.
 * This is robust across different versions of the google-genai library.
 */
@Service
public class GeminiService {

    private final Client geminiClient;
    private final ObjectMapper objectMapper;
    
    // Using the modern, fast model
    private static final String MODEL_NAME = "gemini-2.0-flash";

   
    public GeminiService(@Value("${google.api.key}") String apiKey) {
        this.geminiClient = Client.builder().apiKey(apiKey).build();
        
        this.objectMapper = new ObjectMapper(); 
    }

    public int calculateTotalQuestions(String plan, int topicsCount) {

        int baseQuestions = switch(plan.toLowerCase()) {
            case "b" -> 5;
            case "s" -> 6;
            case "g" -> 9;
            default -> 5;
        };

        double multiplier = switch(topicsCount) {
            case 1 -> 1.0;
            case 2 -> 1.5;
            case 3 -> 2.0;
            default -> 1.0;
        };

        return (int) Math.round(baseQuestions * multiplier);
    }

    
    private String getPromptBasedOnLevel(String levelRange) {
        return switch (levelRange) {

            case "0-1" ->
                "Ask very basic beginner-friendly questions focusing on fundamentals, basic syntax, theoretical concepts, and simple definitions.";

            case "1-2" ->
                "Ask beginner-to-junior level questions focusing on hands-on basics, simple coding logic, debugging basics, REST API basics, and common real-world scenarios.";

            case "2-3" ->
                "Ask mid-level questions focusing on applied concepts, debugging real issues, understanding frameworks,  and API development.";

            case "3-5" ->
                "Ask upper mid-level questions focusing on design patterns, performance tuning basics, concurrency basics, scalable API design, and deeper framework internals.";

            case "5-8" ->
                "Ask senior-level questions focusing on architecture, high-performance systems, concurrency, multithreading, security, optimization, and advanced framework internals.";

            case "8-10" ->
                "Ask advanced senior/lead-level questions focusing on distributed systems, high-scale microservices, architectural decision-making, async communication patterns, and resilience.";

            case "10+" ->
                "Ask expert architect-level questions focusing on system design, distributed systems at scale, infrastructure decisions, architecture trade-offs, high availability, and leadership-driven technical decisions.";

            default ->
                "Ask general technical questions suitable for software engineers.";
        };
    }

    public String getPromptBasedOnLevelResume(String level) {
        return switch (level) {

            case "0-1" ->
                "Ask very basic beginner-friendly questions STRICTLY based on the resume's content—skills, tools, technologies, academic projects, courses, internships, or certifications. Do NOT ask anything outside the resume.";

            case "1-2" ->
                "Ask junior-level questions derived ONLY from the resume. Focus on hands-on fundamentals, technologies listed, simple problem-solving, debugging basics, and beginner real-world skills mentioned in the resume. Do NOT include anything that is not present in the resume.";

            case "2-3" ->
                "Ask mid-level questions strictly based on the resume. Focus on applied concepts, real project experience, roles, responsibilities, frameworks, APIs, databases, and technologies explicitly mentioned in the resume.";

            case "3-5" ->
                "Ask upper mid-level questions entirely from the resume. Focus on design patterns used, performance considerations from projects, architectural decisions mentioned, concurrency basics, and deeper aspects of the frameworks listed.";

            case "5-8" ->
                "Ask senior-level questions tied ONLY to what is present in the resume. Focus on architecture details of past projects, scalability work, concurrency, multithreading, security, optimization, microservices, and advanced frameworks the candidate has actually used.";

            case "8-10" ->
                "Ask advanced senior/lead-level questions strictly based on the resume. Focus on distributed systems, high-scale microservices, technical decision-making, leadership responsibilities, async patterns, DevOps tools, and anything the resume explicitly shows.";

            case "10+" ->
                "Ask expert architect-level questions rooted ONLY in the resume content. Focus on large-scale system design, cross-team architecture decisions, domain-driven design, distributed systems, cloud architecture, resilience patterns, infra decisions, and leadership-oriented technical responsibilities mentioned.";

            default ->
                "Ask technical questions strictly based on the resume, focusing only on skills, experience, projects, certifications, tools, and technologies mentioned by the candidate.";
        };
    }

    public String getPromptBasedOnLevelJD(String level) {
        return switch (level) {

            case "0-1" ->
                "Ask simple beginner-level questions that match the skills and responsibilities described in the job description.";

            case "1-2" ->
                "Ask junior-level questions based ONLY on the job description, focusing on the tools, frameworks, and expectations listed.";

            case "2-3" ->
                "Ask mid-level questions tied strictly to the technologies, responsibilities, and real-world tasks described in the job description.";

            case "3-5" ->
                "Ask upper mid-level questions focused on architecture basics, design patterns, scalability, performance, and deeper concepts mentioned in the JD.";

            case "5-8" ->
                "Ask senior-level questions aligned with the JD, focusing on system design, architectural ownership, leadership responsibilities, scalability, and complex technical expectations.";

            case "8-10" ->
                "Ask advanced senior/lead-level questions based strictly on the JD, emphasizing distributed systems, high-scale architecture, decision-making, and cross-team responsibilities mentioned.";

            case "10+" ->
                "Ask expert architect-level questions rooted ONLY in the JD. Focus on enterprise systems, architectural design decisions, infrastructure, distributed systems, cloud, and leadership elements in the JD.";

            default ->
                "Ask general difficulty questions strictly aligned with the job description.";
        };
    }

    
    public List<String> askGeminiForQuestions(String level, String[] domains,String plan) {
//        System.out.println(plan);
    	
    	 int questionCount =calculateTotalQuestions(plan, domains.length);
    	 String levelPrompt = getPromptBasedOnLevel(level);
        String domainsString = String.join(", ", domains);
        System.err.println(questionCount);
        String prompt = String.format(
        	    "Generate %d technical interview questions for a candidate with experience level %s years. " +
        	    "Difficulty rules: %s " +
        	    "Focus strictly on the following selected topics: %s. " +
        	    "Questions must deeply relate to the selected topics only. " +
        	    "Return ONLY a raw JSON array of strings, like [\"Question 1\", \"Question 2\", ...]. " +
        	    "Do NOT include Markdown formatting, backticks, explanations, titles, or any extra text.",
        	    questionCount,
        	    level,
        	    levelPrompt,
        	    domainsString
        	);

        
        GenerateContentConfig config = GenerateContentConfig.builder()
            .responseMimeType("application/json") 
            .build();

        try {
            // Call the Gemini API 
            GenerateContentResponse response = geminiClient.models.generateContent(
                MODEL_NAME,
                prompt,
                config 
            );
           
            String rawJson = response.text();
            
            // Clean up potential Markdown code blocks if the model adds them despite instructions
            if (rawJson.startsWith("```json")) {
                rawJson = rawJson.replace("```json", "").replace("```", "");
            } else if (rawJson.startsWith("```")) {
                rawJson = rawJson.replace("```", "");
            }
            
            // Deserialize the cleaned raw JSON response into a List<String>
            List<String> questionsList = objectMapper.readValue(
                rawJson, 
                new TypeReference<List<String>>() {}
            );

            return questionsList;

        } catch (Exception ex) {
            System.err.println("Gemini API Error: " + ex.getMessage());
            ex.printStackTrace();
            // Return an error list for graceful failure so the frontend doesn't crash
            return List.of("Error: Could not get response from Gemini API (" + ex.getMessage() + ")");
        }
    }
    
    public List<String> askGeminiForResumeQuestions(String level, String resume,String plan) {
//      System.out.println(plan);
  	
  	 int questionCount =3;
//  			 calculateTotalQuestions(plan, domains.length);
  	 String levelPrompt = getPromptBasedOnLevelResume(level);
//      System.err.println(questionCount);
  	String prompt = String.format(
  		    "You are an expert technical interviewer. Read the following resume text carefully:\n\n" +
  		    "----- RESUME START -----\n%s\n----- RESUME END -----\n\n" +
  		    "Based on ONLY the content found inside the resume, generate %d highly relevant technical interview questions. " +
  		    "The questions must deeply relate to the candidate’s skills, tech stack, projects, achievements, and experience mentioned. " +
  		    "Experience level of the candidate is: %s years. Difficulty rules: %s\n\n" +
  		    "Return ONLY a raw JSON array of strings, for example: [\"Question 1\", \"Question 2\", ...]. " +
  		    "Do NOT include Markdown, backticks, explanations, titles, or any extra text outside the JSON array.",
  		    resume,
  		    questionCount,
  		    level,
  		    levelPrompt
  		);


      
      GenerateContentConfig config = GenerateContentConfig.builder()
          .responseMimeType("application/json") 
          .build();

      try {
          // Call the Gemini API 
          GenerateContentResponse response = geminiClient.models.generateContent(
              MODEL_NAME,
              prompt,
              config 
          );
         
          String rawJson = response.text();
          
          // Clean up potential Markdown code blocks if the model adds them despite instructions
          if (rawJson.startsWith("```json")) {
              rawJson = rawJson.replace("```json", "").replace("```", "");
          } else if (rawJson.startsWith("```")) {
              rawJson = rawJson.replace("```", "");
          }
          
          // Deserialize the cleaned raw JSON response into a List<String>
          List<String> questionsList = objectMapper.readValue(
              rawJson, 
              new TypeReference<List<String>>() {}
          );

          return questionsList;

      } catch (Exception ex) {
          System.err.println("Gemini API Error: " + ex.getMessage());
          ex.printStackTrace();
          // Return an error list for graceful failure so the frontend doesn't crash
          return List.of("Error: Could not get response from Gemini API (" + ex.getMessage() + ")");
      }
  }
  
    public List<String> askGeminiForJDQuestions(String level, String jd,String plan) {
//      System.out.println(plan);
  	
  	 int questionCount =3;
//  			 calculateTotalQuestions(plan, domains.length);
  	 String levelPrompt = getPromptBasedOnLevelJD(level);
//      System.err.println(questionCount);
  	  String prompt = String.format(
  	        "You are an expert technical interviewer. Carefully analyze the following JOB DESCRIPTION:\n\n" +
  	        "----- JOB DESCRIPTION START -----\n%s\n----- JOB DESCRIPTION END -----\n\n" +
  	        "Generate %d highly relevant technical interview questions based STRICTLY on the job description above. " +
  	        "Do NOT use any external knowledge beyond what is stated in the JD. " +
  	        "The questions must directly relate to the required skills, responsibilities, tech stack, expectations, and competencies mentioned.\n\n" +
  	        "Experience level for difficulty scaling: %s years. Difficulty rules: %s\n\n" +
  	        "Return ONLY a raw JSON array of strings, e.g., [\"Question 1\", \"Question 2\", ...]. " +
  	        "Do NOT include Markdown, backticks, titles, comments, or any text outside the JSON array.",
  	        jd,
  	        questionCount,
  	        level,
  	        levelPrompt
  	    );


      
      GenerateContentConfig config = GenerateContentConfig.builder()
          .responseMimeType("application/json") 
          .build();

      try {
          // Call the Gemini API 
          GenerateContentResponse response = geminiClient.models.generateContent(
              MODEL_NAME,
              prompt,
              config 
          );
         
          String rawJson = response.text();
          
          // Clean up potential Markdown code blocks if the model adds them despite instructions
          if (rawJson.startsWith("```json")) {
              rawJson = rawJson.replace("```json", "").replace("```", "");
          } else if (rawJson.startsWith("```")) {
              rawJson = rawJson.replace("```", "");
          }
          
          // Deserialize the cleaned raw JSON response into a List<String>
          List<String> questionsList = objectMapper.readValue(
              rawJson, 
              new TypeReference<List<String>>() {}
          );

          return questionsList;

      } catch (Exception ex) {
          System.err.println("Gemini API Error: " + ex.getMessage());
          ex.printStackTrace();
          // Return an error list for graceful failure so the frontend doesn't crash
          return List.of("Error: Could not get response from Gemini API (" + ex.getMessage() + ")");
      }
  } 
    
    public Map<String, Object> getInterviewFeedback(String qnaHistoryJson) {
        // Construct a prompt that embeds the user's Q&A history and defines the strict output schema.
        String prompt = String.format("""
            You are an expert technical interviewer. Analyze the following interview Question and Answer history:
            
            %s
            
            Based on this interaction, provide detailed feedback in the following STRICT JSON format:
            {
                "scores": {
                    "technicalScore": (float 0-10),
                    "communicationScore": (float 0-10),
                    "voiceClarityScore": (float 0-10, estimate based on text clarity/grammar),
                    "overallScore": (float 0-10)
                },
                "strengths": ["strength 1", "strength 2", ...],
                "improvements": ["improvement 1", "improvement 2", ...],
                "verdict": "A summary verdict string.",
                "questionFeedback": [
                    {
                        "question": "The exact question text",
                        "feedback": "Specific critique of the answer provided."
                    }
                ]
            }
            
            Return ONLY the raw JSON. Do not include markdown formatting or code blocks and as the answers are 
            from spoke mode so no code snippet couldbe given and also spelling mistake checking is 
            not required and also catch similar speak words.
            """, qnaHistoryJson);

        GenerateContentConfig config = GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .build();

        try {
            // Call Gemini
            GenerateContentResponse response = geminiClient.models.generateContent(MODEL_NAME, prompt, config);
            String rawJson = cleanJson(response.text());

            // Deserialize into the requested Map structure
            return objectMapper.readValue(rawJson, new TypeReference<Map<String, Object>>() {});

        } catch (Exception ex) {
            System.err.println("Error getting feedback: " + ex.getMessage());
            ex.printStackTrace();
            // Return a simple error map
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Could not generate feedback: " + ex.getMessage());
            return errorResponse;
        }
    }

    // Helper to remove potential Markdown formatting from the response
    private String cleanJson(String rawJson) {
        if (rawJson.startsWith("```json")) {
            return rawJson.replace("```json", "").replace("```", "");
        } else if (rawJson.startsWith("```")) {
            return rawJson.replace("```", "");
        }
        return rawJson;
    }
    
    
    
}