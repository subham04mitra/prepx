package com.exam.Entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

import java.time.Instant;

@Data
@Document(collection = "mas_subscription")
public class MasSubscription {

	@Id 
    private String id;

    @Field("sub_type")
    private String subType;    

    @Field("sub_name")
    private String subName;
    
    @Field("limit")
    private int limit;
    

   
}

