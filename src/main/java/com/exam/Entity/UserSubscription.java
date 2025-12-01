package com.exam.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "user_vs_sub")
public class UserSubscription {

	@Id 
	public String id;
	
    @Field("uuid")
    private String uuid;

    @Field("sub_type")
    private String subType;    
    
    @Field("count")
    private int count;
    
    @Field("tcount")
    private int tCount;
    
    @Field("rcount")
    private int rCount;
    
    @Field("entry_ts")
    private Instant entryTs;
	
}
