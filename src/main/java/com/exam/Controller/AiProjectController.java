package com.exam.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exam.Response.ApiResponses;
import com.exam.Response.ResponseBean;
import com.exam.Service.AiProjectService;
import com.exam.Service.AuthServiceNew;
import com.exam.Util.GeminiService;
import com.exam.reqDTO.CommonReqModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/ai")
public class AiProjectController {

	@Autowired
	GeminiService geminiService;

	@Autowired
	AuthServiceNew authserv;
	@Autowired
	AiProjectService aiservice;
	ResponseBean responseBean = new ResponseBean();

	@PostMapping("/Get-Qs")
	public ResponseEntity<?> GetQsList(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {

		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = aiservice.getQsListService(model, responseBean, authToken);

		return finalResponse;

	}

	@PostMapping(value = "/Resume-Parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> ResumeAnalyzeList(@RequestParam("file") MultipartFile file,
			@RequestHeader("Authorization") String authorizationHeader) throws Exception {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = aiservice.ResumeParseService(file, responseBean, authToken);

		return finalResponse;

	}

	@PostMapping(value = "/Resume-Parse-JD", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> ResumeJDMatcher(@RequestParam("file") MultipartFile file,
			@RequestParam("jd") String jobDescription, @RequestHeader("Authorization") String authorizationHeader)
			throws Exception {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = aiservice.ResumeJDParseService(file, jobDescription, responseBean, authToken);

		return finalResponse;

	}

	@PostMapping("/Get-Qs-Role")
	public ResponseEntity<?> GetQsListRoleBsed(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {

		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = aiservice.getQsListRoleBasedService(model, responseBean, authToken);

		return finalResponse;

	}

	@PostMapping("/get-portfolio-by-slug")
	public ResponseEntity<ApiResponses> getPortfolioController(@RequestBody CommonReqModel model) {

		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.getPortfolioService(model, responseBean);

		return finalResponse;
	}

	@PostMapping("/Submit-Ans")
	public ResponseEntity<?> GetFeedback(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {

		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = aiservice.getFeedbackService(model, responseBean, authToken);

		return finalResponse;

	}

	@PostMapping("/Ai-Roadmap")
	public ResponseEntity<?> GetRoadmap(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {

		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = aiservice.getAiRoadMapService(model, responseBean, authToken);

		return finalResponse;

	}

//	@GetMapping("/ask")
//	 public List<String> ask() {
//        // Hardcoded data
//        String level = "2-4";
//        String[] domains = {"Spring Boot", "Java"};
//
//        return geminiService.askGeminiForQuestions(level, domains);
//    }
}
