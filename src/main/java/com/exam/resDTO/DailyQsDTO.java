package com.exam.resDTO;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class DailyQsDTO {

	    private long id;
	   
	    private String lang;
	    
	    private String question;
	    private String submit;
	   	  
	    private String[] options;
}
