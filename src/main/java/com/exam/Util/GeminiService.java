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


    public List<String> askGeminiForQuestions(String level, String[] domains) {
        
        String domainsString = String.join(", ", domains);
        
        String prompt = String.format(
            "Generate 2 technical interview questions for a candidate with %s year of experience, focusing on the following domains: %s. " +
            "Strictly return ONLY a raw JSON array of strings. " +
            "Example format: [\"Question 1\", \"Question 2\"]. " +
            "Do not include Markdown formatting like ```json ... ``` or any introductory text.",
            level, 
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
            
            Return ONLY the raw JSON. Do not include markdown formatting or code blocks.
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