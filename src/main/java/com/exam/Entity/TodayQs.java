package com.exam.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "today_qs")
public class TodayQs {

	@Id 
    private String id;
	

    @Field("qs_id")
    private long qsId;
    
    @Field("date")
    private Instant date;
    
}