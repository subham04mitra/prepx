package com.exam.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "mas_otp")
public class OTP {

	@Id 
    private String id;
	
	@Field("uuid")
    private String uuid;
	
	@Field("email_otp")
    private int emailOtp;
	
	
}
