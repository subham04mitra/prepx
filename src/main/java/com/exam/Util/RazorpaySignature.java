package com.exam.Util;

import com.razorpay.Utils;
import java.util.*;

import org.json.JSONObject;

public class RazorpaySignature {

	public static boolean verifySignature(String orderId, String paymentId, String signature, String secret) {
		try {
			JSONObject options = new JSONObject();
			options.put("razorpay_order_id", orderId);
			options.put("razorpay_payment_id", paymentId);
			options.put("razorpay_signature", signature);

			return Utils.verifyPaymentSignature(options, secret);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
