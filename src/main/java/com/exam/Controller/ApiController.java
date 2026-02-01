package com.exam.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exam.Response.ApiResponses;
import com.exam.Response.ResponseBean;
import com.exam.Service.AuthServiceNew;
import com.exam.Util.RazorpaySignature;
import com.exam.reqDTO.CommonReqModel;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/mas")
public class ApiController {

	@Value("${razorpay.key.id}")
	private String RAZORPAY_KEY_ID;

	@Value("${razorpay.key.secret}")
	private String RAZORPAY_KEY_SECRET;

	@Autowired
	AuthServiceNew authserv;
	ResponseBean responseBean = new ResponseBean();

	@GetMapping("/bill-list")
	public ResponseEntity<ApiResponses> billListController(@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.getbillListService(responseBean, authToken);

		return finalResponse;
	}

	@PostMapping("/check-subscribe")
	public ResponseEntity<ApiResponses> checksubscribeController(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.checksubscribeService(responseBean, model, authToken);

		return finalResponse;
	}

	@GetMapping("/profile")
	public ResponseEntity<ApiResponses> profileController(@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.profileService(responseBean, authToken);

		return finalResponse;
	}

	@GetMapping("/records")
	public ResponseEntity<ApiResponses> ViewRecordsController(@RequestParam(name = "type", required = true) String type,
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.viewRecordsService(responseBean, authToken, type);

		return finalResponse;
	}

	@GetMapping("/leaderboard")
	public ResponseEntity<ApiResponses> LeaderBoardController(
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.leaserboardService(responseBean, authToken);

		return finalResponse;
	}

	@GetMapping("/daily-qs")
	public ResponseEntity<ApiResponses> GetDailyQSController(
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.viewDailyQSService(responseBean, authToken);

		return finalResponse;
	}

	@PostMapping("/save-feedback")
	public ResponseEntity<ApiResponses> saveUserFeedbackController(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.saveFeedbackService(responseBean, model, authToken);

		return finalResponse;
	}

	@PostMapping("/save-profile")
	public ResponseEntity<ApiResponses> compeletProfileController(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.completeProfileService(responseBean, model, authToken);

		return finalResponse;
	}

	@PostMapping("/save-portfolio")
	public ResponseEntity<ApiResponses> savePortfolioController(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.savePortfolioService(responseBean, model, authToken);

		return finalResponse;
	}

	@GetMapping("/get-profile")
	public ResponseEntity<ApiResponses> getcompeletProfileController(
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.getcompleteProfileService(responseBean, authToken);

		return finalResponse;
	}

	@PostMapping("/save-dailyqs")
	public ResponseEntity<ApiResponses> saveDailyQsController(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.saveDailQSService(responseBean, model, authToken);

		return finalResponse;
	}

	@PostMapping("/Purchase-Interview")
	public ResponseEntity<ApiResponses> purchaseInterviewController(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.purchaseInterviewService(responseBean, model, authToken);

		return finalResponse;
	}

	@PostMapping("/create-order")
	public Map<String, Object> createOrder(@RequestBody Map<String, Object> data) throws Exception {
		String planId = (String) data.get("planId");

		// 1. Calculate amount based on planId on the SERVER SIDE
		int amountInRupees = 0;
		if ("B".equals(planId))
			amountInRupees = 9;
		else if ("S".equals(planId))
			amountInRupees = 29;
		else if ("G".equals(planId))
			amountInRupees = 49;

		// Razorpay expects amount in PAISE (multiply by 100)
		int amountInPaise = amountInRupees * 100;

		RazorpayClient client = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);

		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", amountInPaise);
		orderRequest.put("currency", "INR");
		orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

		Order order = client.orders.create(orderRequest);

		// Return details to Frontend
		return Map.of("id", order.get("id"), "amount", order.get("amount"), "currency", "INR", "key", RAZORPAY_KEY_ID);
	}

	@PostMapping("/verify-payment")
	public ResponseEntity<ApiResponses> verifyPayment(@RequestBody Map<String, String> data,
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];

//    	 String authToken = authorizationHeader.split(" ")[1];

		String orderId = data.get("razorpay_order_id");
		String paymentId = data.get("razorpay_payment_id");
		String signature = data.get("razorpay_signature");
		String planId = data.get("planId");

		Map<String, Object> rzrMap = new HashMap<>();
		rzrMap.put("razorpay_order_id", orderId);
		rzrMap.put("razorpay_payment_id", paymentId);
		rzrMap.put("razorpay_signature", signature);
		rzrMap.put("planId", planId);
		rzrMap.put("amount", data.get("amount"));

//    	    
		ResponseEntity<ApiResponses> finalResponse;
		finalResponse = authserv.subscribeService(responseBean, authToken, rzrMap);

		return finalResponse;
	}

	@PostMapping("/get-mock-apti")
	public ResponseEntity<ApiResponses> mockaptiQsController(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.getmockAptiQsService(responseBean, model, authToken);

		return finalResponse;
	}

	@PostMapping("/submit-mock-apti")
	public ResponseEntity<ApiResponses> submitmockaptiQsController(@RequestBody CommonReqModel model,
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.submitmockAptiQsService(responseBean, model, authToken);

		return finalResponse;
	}
	
	
	@GetMapping("/view-apti-records")
	public ResponseEntity<ApiResponses> viewAptiRecordsController(
			@RequestHeader("Authorization") String authorizationHeader) {
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;

		finalResponse = authserv.getAptiFeedbackByUuidService(responseBean,  authToken);

		return finalResponse;
	}

}