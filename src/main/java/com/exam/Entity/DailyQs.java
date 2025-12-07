package com.exam.Entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "daily_qs")
public class DailyQs {

	@Id 
    private String id;
	

    @Field("qs_id")
    private long qsId;
    
    @Field("lang")
    private String lang;
    
    @Field("question")
    private String question;
    
    @Field("option1")
    private String option1;

    @Field("option2")
    private String option2;

    @Field("option3")
    private String option3;

    @Field("option4")
    private String option4; 

    @Field("answer")
    private String answer;
    
	
}
