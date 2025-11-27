package com.exam.Response;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ApiResponses {

	private String code;
	private String message;
	private Object data;
	private String token;
	
}
