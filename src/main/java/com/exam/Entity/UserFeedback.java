package com.exam.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "user_feedback")
public class UserFeedback {

	@Id 
    private String id;

    @Field("uuid")
    private String uuid;
    
    @Field("rating")
    private int rating;
    
    @Field("feedback")
    private String feedback;
}
