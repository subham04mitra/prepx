package com.exam.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "payment_data")
public class PaymentData {


	@Id 
	public String id;
	
    @Field("uuid")
    private String uuid;
    
    @Field("order_id")
    private String orderId;
    @Field("payment_id")
    private String paymentId;
    @Field("signature")
    private String signature;
    @Field("amount")
    private String amount;
    
    @Field("entry_ts")
    private Instant entryTs;
    
}
