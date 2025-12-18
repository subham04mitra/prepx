package com.exam.Util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Groq-based Interview AI Service
 * Model: llama-3.1-8b-instant
 * Strategy: Prompt-based strict JSON output
 */
@Service
public class GeminiService {

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL_NAME = "llama-3.1-8b-instant";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;

    public GeminiService(@Value("${groq.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    /* ===================== COMMON UTIL ===================== */

    private String callGroq(String prompt) throws Exception {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_NAME);

        // 🔥 RANDOMNESS SETTINGS
        requestBody.put("temperature", 0.9);
        requestBody.put("top_p", 0.95);
        requestBody.put("frequency_penalty", 0.4);
        requestBody.put("presence_penalty", 0.3);
        requestBody.put("max_tokens", 2048);

        requestBody.put("messages", List.of(
                Map.of("role", "system", "content",
                       "You are a strict JSON-only API. Always generate fresh, non-repetitive questions."),
                Map.of("role", "user", "content", prompt)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(GROQ_URL, entity, Map.class);

        Map<?, ?> body = response.getBody();
        List<?> choices = (List<?>) body.get("choices");
        Map<?, ?> message = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");

        return cleanJson(message.get("content").toString());
    }

    private String cleanJson(String raw) {
        if (raw.startsWith("```")) {
            return raw.replaceAll("```json|```", "").trim();
        }
        return raw.trim();
    }

    /* ===================== QUESTION COUNT ===================== */

    public int calculateTotalQuestions(String plan, int topicsCount) {

        int base = switch (plan.toLowerCase()) {
            case "b" -> 5;
            case "s" -> 6;
            case "g" -> 9;
            default -> 5;
        };

        double multiplier = switch (topicsCount) {
            case 2 -> 1.5;
            case 3 -> 2.0;
            default -> 1.0;
        };

        return (int) Math.round(base * multiplier);
    }

    /* ===================== LEVEL PROMPTS ===================== */

    private String getPromptBasedOnLevel(String level) {
        return switch (level) {
            case "0-1" -> "Ask very basic beginner-friendly questions.";
            case "1-2" -> "Ask beginner-to-junior level questions.";
            case "2-3" -> "Ask mid-level applied concept questions.";
            case "3-5" -> "Ask upper mid-level design and framework questions.";
            case "5-8" -> "Ask senior-level architecture and optimization questions.";
            case "8-10" -> "Ask advanced lead-level distributed systems questions.";
            case "10+" -> "Ask expert architect-level system design questions.";
            default -> "Ask general technical questions.";
        };
    }

    /* ===================== DOMAIN QUESTIONS ===================== */

    public List<String> askGeminiForQuestions(String level, String[] domains, String plan) {

        int count = calculateTotalQuestions(plan, domains.length);
        String prompt = String.format("""
                Generate %d technical interview questions.
                Experience: %s years.
                Difficulty: %s
                Topics: %s
                Return ONLY JSON array of strings.
                """,
                count,
                level,
                getPromptBasedOnLevel(level),
                String.join(", ", domains)
        );

        try {
            String json = callGroq(prompt);
            System.out.println(json);
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }

    /* ===================== ROLE QUESTIONS ===================== */

    public List<String> askGeminiForRoleBasedQuestions(String level, String role[]) {
    	System.out.println(role);
        String prompt = String.format("""
                Generate 10 technical interview questions.
                Role: %s
                Experience: %s years.
                Difficulty: %s
                Return ONLY JSON array of strings.
                """,
                role[0],
                level,
                getPromptBasedOnLevel(level)
        );

        try {
            String json = callGroq(prompt);
            System.err.println(json);
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }

    /* ===================== RESUME QUESTIONS ===================== */

    public List<String> askGeminiForResumeQuestions(String level, String resume, String plan) {

        String prompt = String.format("""
                Resume:
                %s

                Generate 3 technical interview questions strictly from resume.
                Experience: %s
                Difficulty: %s
                Return ONLY JSON array.
                """,
                resume,
                level,
                getPromptBasedOnLevel(level)
        );

        try {
            String json = callGroq(prompt);
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }

    /* ===================== JD QUESTIONS ===================== */

    public List<String> askGeminiForJDQuestions(String level, String jd, String plan) {

        String prompt = String.format("""
                Job Description:
                %s

                Generate 3 technical interview questions strictly from JD.
                Experience: %s
                Difficulty: %s
                Return ONLY JSON array.
                """,
                jd,
                level,
                getPromptBasedOnLevel(level)
        );

        try {
            String json = callGroq(prompt);
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }

    /* ===================== INTERVIEW FEEDBACK ===================== */

    public Map<String, Object> getInterviewFeedback(String qnaHistoryJson) {

        String prompt = String.format("""
                Analyze the interview Q&A:
                %s

                Return STRICT JSON:
                {
                  "scores": {
                    "technicalScore": 0-10,
                    "communicationScore": 0-10,
                    "voiceClarityScore": 0-10,
                    "overallScore": 0-10
                  },
                  "strengths": [],
                  "improvements": [],
                  "verdict": "",
                  "questionFeedback": [
                    {"question":"","feedback":""}
                  ]
                }

                Ignore grammar/spelling mistakes.
                """,
                qnaHistoryJson
        );

        try {
            String json = callGroq(prompt);
//            System.err.println(json);
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }

    
    public Map<String, Object> askGeminiForResumeAnalyze(String resumeText) {
        // We explicitly ask for the JSON keys needed by the React frontend
        String prompt = String.format("""
            You are a highly precise ATS (Applicant Tracking System) Analyzer. 
You will be provided with raw text extracted from a resume.

CRITICAL INSTRUCTIONS:
1. DO NOT claim a section is missing (like Summary or Experience) until you have scanned the ENTIRE text for those specific keywords or synonyms (e.g., 'Professional Profile', 'Work History').
2. Ignore minor formatting artifacts caused by PDF-to-text conversion (like strange line breaks or bullet symbols).
3. If you find duplicate phrases (e.g., 'RBAC systems, RBAC systems'), flag it as a content error, NOT a structural error.
4. Analyze based on content logic, not just visual layout.

Input Text:
%s

Return STRICT JSON format ONLY. Do not include markdown like ```json.
And Also Give issues with solutions for each RESUME TAILORING,Header Links,Email Address
,Design,ATS Essentials,Contact Information,
SECTIONS,Spelling & Grammar,Repetition,Quantify Impact
Required Format:
{
  "atsScore": (0-100),
  "summary": "overview",
  "issues": [
    { "error": "Actual content mistake", "solution": "How to fix" }
  ],
  "formattingTips": []
}"
                Required Structure:
                {
                  "atsScore": (0-100 integer),
                  "summary": "2-sentence professional overview",
                  "grammarScore": (0-100 integer),
                  "issues": [
                    { "error": "Description of mistake", "solution": "How to fix it" }
                  ],
                  "formattingTips": ["Tip 1", "Tip 2", "Tip 3"]
                }
                """, resumeText);

        try {
            // Call your AI (Gemini/Groq)
            String jsonResponse = callGroq(prompt); 
            
            // Use ObjectMapper to convert the String to a Map for the response
            return objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        } catch (Exception e) {
            System.err.println("AI Parsing Error: " + e.getMessage());
            return null;
        }
    }
    
    
    public Map<String, Object> getJDMatchInsights(String resumeText, String jobDescription) {
        String prompt = String.format("""
           "Analyze the following Resume against the Job Description (JD) provided. 

RESUME TEXT: 
%s

JOB DESCRIPTION:
%s

Perform the analysis across these 4 dimensions:
1. KEYWORD ALIGNMENT: Identify matched and missing technical/soft skills.
2. EXPERIENCE GAP: Compare the required years of experience and seniority level.
3. PROJECT RELEVANCE: Evaluate if the candidate's previous projects (e.g., World Bank, E-Governance) match the industry of the JD.
4. ACTIONABLE STEPS: Provide concrete, non-generic advice to bridge the gap.

RESPONSE STRUCTURE (Strict JSON): Do not include markdown like ```json.s
{
  "matchPercentage": (Integer 0-100),
  "matchSummary": "A high-level executive summary of the alignment.",
  "matchedSkills": ["Skill A", "Skill B"],
  "missingSkills": ["Critical Skill C", "Nice-to-have Skill D"],
  "analysisBreakdown": {
    "technicalFit": "Detailed analysis of tech stack alignment",
    "seniorityFit": "Analysis of experience level vs requirements",
    "domainFit": "Evaluation of industry-specific experience"
  },
  "actionPlan": [
    "Specific improvement 1",
    "Specific improvement 2",
    "Specific improvement 3"
  ],
  "fruitfulInsights": "One unique 'pro-tip' to make the resume stand out for this specific role."
}"
            """, resumeText, jobDescription);

        try {
            String json = callGroq(prompt);
            // Clean JSON if AI adds markdown
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return null;
        }
    }
    
    
}
