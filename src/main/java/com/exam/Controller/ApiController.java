package com.exam.Controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.Response.ApiResponses;
import com.exam.Response.ResponseBean;
import com.exam.Service.AuthServiceNew;
import com.exam.reqDTO.CommonReqModel;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

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
	ResponseBean responseBean=new ResponseBean();
	
	@PostMapping("/subscribe")
	public ResponseEntity<ApiResponses> subscribeController(@RequestBody CommonReqModel model,@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.subscribeService(responseBean,model,authToken);
		
		return finalResponse;
}
	@PostMapping("/check-subscribe")
	public ResponseEntity<ApiResponses> checksubscribeController(@RequestBody CommonReqModel model,@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.checksubscribeService(responseBean,model,authToken);
		
		return finalResponse;
}
	
	@GetMapping("/profile")
	public ResponseEntity<ApiResponses> profileController(@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.profileService(responseBean,authToken);
		
		return finalResponse;
}
	
	
	@GetMapping("/records")
	public ResponseEntity<ApiResponses> ViewRecordsController(@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.viewRecordsService(responseBean,authToken);
		
		return finalResponse;
}
	
	
	@GetMapping("/leaderboard")
	public ResponseEntity<ApiResponses> LeaderBoardController(@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.leaserboardService(responseBean,authToken);
		
		return finalResponse;
}
	
	
	@GetMapping("/daily-qs")
	public ResponseEntity<ApiResponses> GetDailyQSController(@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.viewDailyQSService(responseBean,authToken);
		
		return finalResponse;
}
	
	
	
	@PostMapping("/save-feedback")
	public ResponseEntity<ApiResponses> saveUserFeedbackController(@RequestBody CommonReqModel model,@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.saveFeedbackService(responseBean,model,authToken);
		
		return finalResponse;
}
	
	

	@PostMapping("/save-dailyqs")
	public ResponseEntity<ApiResponses> saveDailyQsController(@RequestBody CommonReqModel model,@RequestHeader("Authorization") String authorizationHeader){
		String authToken = authorizationHeader.split(" ")[1];
		ResponseEntity<ApiResponses> finalResponse;
		
		finalResponse=authserv.saveDailQSService(responseBean,model,authToken);
		
		return finalResponse;
}
	
	
	@PostMapping("/create-order")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> data) throws Exception {
        String planId = (String) data.get("planId");
        
        // 1. Calculate amount based on planId on the SERVER SIDE
        int amountInRupees = 0;
        if ("B".equals(planId)) amountInRupees = 9;
        else if ("S".equals(planId)) amountInRupees = 29;
        else if ("G".equals(planId)) amountInRupees = 49;
        
        // Razorpay expects amount in PAISE (multiply by 100)
        int amountInPaise = amountInRupees * 100;

        RazorpayClient client = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = client.orders.create(orderRequest);

        // Return details to Frontend
        return Map.of(
            "id", order.get("id"),
            "amount", order.get("amount"),
            "currency", "INR",
            "key", RAZORPAY_KEY_ID
        );
    }

    @PostMapping("/verify-payment")
    public Map<String, String> verifyPayment(@RequestBody Map<String, String> data) {
        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");
        String planId = data.get("planId");

        // 1. Verify Signature (Use Utils provided by Razorpay)
        // boolean isValid = Utils.verifyPaymentSignature(data, RAZORPAY_KEY_SECRET);
        
        // 2. If Valid: Update User in Database to 'planId'
        
        return Map.of("status", "success");
    }
	
	
}