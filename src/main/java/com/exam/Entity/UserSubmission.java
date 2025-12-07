package com.exam.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "user_submission")
public class UserSubmission {

	@Id 
    private String id;
	

    @Field("qs_id")
    private long qsId;
    
    @Field("uuid")
    private String uuid;
    
    @Field("date")
    private Instant date;
	
}
