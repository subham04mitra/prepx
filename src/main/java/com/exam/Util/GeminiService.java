package com.exam.Util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Groq-based Interview AI Service Model: llama-3.1-8b-instant Strategy:
 * Prompt-based strict JSON output
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

		requestBody.put("messages",
				List.of(Map.of("role", "system", "content",
						"You are a strict JSON-only API. Always generate fresh, non-repetitive questions."),
						Map.of("role", "user", "content", prompt)));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, entity, Map.class);

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
				""", count, level, getPromptBasedOnLevel(level), String.join(", ", domains));

		try {
			String json = callGroq(prompt);
			System.out.println(json);
			return objectMapper.readValue(json, new TypeReference<>() {
			});
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
				""", role[0], level, getPromptBasedOnLevel(level));

		try {
			String json = callGroq(prompt);
			System.err.println(json);
			return objectMapper.readValue(json, new TypeReference<>() {
			});
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
				""", resume, level, getPromptBasedOnLevel(level));

		try {
			String json = callGroq(prompt);
			return objectMapper.readValue(json, new TypeReference<>() {
			});
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
				""", jd, level, getPromptBasedOnLevel(level));

		try {
			String json = callGroq(prompt);
			return objectMapper.readValue(json, new TypeReference<>() {
			});
		} catch (Exception e) {
			return null;
		}
	}

	/* ===================== INTERVIEW FEEDBACK ===================== */

	public Map<String, Object> getInterviewFeedback(String qnaHistoryJson) {

		String prompt = String.format(
				"""
						               Act as an expert Technical Recruiter and Communication Coach.

						### Task
						Analyze the provided Interview Q&A transcript. Note that the text was generated via voice-to-text, so there will be significant typos, phonetic misspellings (e.g., "java script" instead of "JavaScript", "spring board" instead of "Spring Boot"), and grammatical errors.

						### Instructions
						1. IGNORE all spelling and grammatical mistakes caused by transcription.
						2. Focus on the depth of technical knowledge, clarity of thought, and professional tone.
						3. Calculate scores out of 10 based on the quality of the answers provided.
						4. The "overallScore" must be a weighted average of the technical and communication performance not voice clarity score.

						### Input Data
						%s

						### Response Format
						You must return ONLY a valid JSON object. Do not include markdown formatting like ```json ... ``` or any introductory text.

						{
						  "scores": {
						    "technicalScore": 0-10,
						    "communicationScore": 0-10,
						    "voiceClarityScore": 0-10,
						    "overallScore": 0-10
						  },
						  "strengths": ["string"],
						  "improvements": ["string"],
						  "verdict": "string",
						  "questionFeedback": [
						    {"question": "string", "feedback": "string"}
						  ]
						}
						                """,
				qnaHistoryJson);

		try {
			String json = callGroq(prompt);
//            System.err.println(json);
			return objectMapper.readValue(json, new TypeReference<>() {
			});
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, Object> getAiRoadmap(String topic, List<String> improvements) {
		String prompt = String.format("""
				You are a Senior Technical Architect and Career Coach at PrepXAI.
				The user recently performed poorly in an interview on the topic: "%s".
				The specific gaps identified were: %s.

				TASK:
				Generate a comprehensive, professional mastery roadmap to help the user become a Pro in this topic.

				REQUIRED JSON STRUCTURE (STRICT JSON ONLY):
				{
				  "topic": "The main topic",
				  "strategy": "A 2-sentence professional strategy for mastery",
				  "weeklyPlan": [
				    {
				      "week": 1,
				      "focus": "Conceptual depth",
				      "tasks": ["Task 1", "Task 2", "Task 3"],
				      "outcome": "What they will achieve this week"
				    }
				  ],
				  "projects": [
				    {
				      "title": "Project Name",
				      "difficulty": "Intermediate/Advanced",
				      "description": "Short description of a project that covers the identified gaps",
				      "features": ["Feature A", "Feature B"]
				    }
				  ],
				  "masteryTips": ["Professional tip 1", "Professional tip 2"]
				}

				CRITICAL INSTRUCTIONS:
				1. The plan must be 4 weeks long.
				2. Ensure the projects specifically address the improvement areas: %s.
				3. Do not include markdown code blocks. Return raw JSON string.
				""", topic, String.join(", ", improvements), String.join(", ", improvements));

		try {
			// Call your AI utility (Groq/Gemini)
			String jsonResponse = callGroq(prompt);
			return objectMapper.readValue(jsonResponse, new TypeReference<>() {
			});
		} catch (Exception e) {
			System.err.println("Roadmap Generation Error: " + e.getMessage());
			return null;
		}
	}

	public Map<String, Object> askGeminiForResumeAnalyze(String resumeText) {
		String prompt = String.format("""
				   You are an elite ATS (Applicant Tracking System) Auditor.
				   Your task is to provide a brutal and honest score based on the provided resume text.

				   SCORING RUBRIC (Calculate the exact total):
				   1. Contact Info (10 pts): Full name, professional email, phone, and LinkedIn/Portfolio.
				   2. Experience (30 pts): Check for Action Verbs and Quantifiable Results (numbers/percentages).
				   3. Skills (20 pts): Presence of industry-specific hard skills and tools.
				   4. Education (10 pts): Degree, Institution, and Graduation .
				   5. Formatting (15 pts): Logical flow, clear headings, and lack of visual noise.
				   6. Grammar & Spelling (15 pts): Penalize 5 pts for every distinct error found.

				   CRITICAL INSTRUCTIONS:
				   - Do NOT hardcode the score. Calculate it based on the criteria above.
				   - If a section is weak (e.g., Experience lacks metrics), deduct points accordingly.
				   - Scan the entire text for synonyms (e.g., 'Work History' instead of 'Experience').

				   Input Text:
				   %s

				   Return STRICT JSON format ONLY. Do not include markdown headers like ```json.
				Give Min 7 Issues
				   Required Structure:
				   {
				     "atsScore": (Calculated integer 0-100),
				     "summary": "2-sentence executive summary of why the score was given",
				     "grammarScore": (Calculated integer 0-100),
				     "issues": [
				       { "error": "Specific mistake found", "solution": "Exact recommendation to improve" }
				     ],
				     "formattingTips": ["Tip 1", "Tip 2", "Tip 3"]
				   }
				   """, resumeText);

		try {
			String jsonResponse = callGroq(prompt);
			return objectMapper.readValue(jsonResponse, new TypeReference<>() {
			});
		} catch (Exception e) {
			System.err.println("AI Analysis Error: " + e.getMessage());
			return null;
		}
	}

	public Map<String, Object> getJDMatchInsights(String resumeText, String jobDescription) {
		String prompt = String.format(
				"""
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
						            """,
				resumeText, jobDescription);

		try {
			String json = callGroq(prompt);
			// Clean JSON if AI adds markdown
			return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			return null;
		}
	}

}
