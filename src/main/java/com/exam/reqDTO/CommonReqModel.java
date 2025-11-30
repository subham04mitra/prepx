package com.exam.reqDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonReqModel {

	
	private String uuid;
	private String user_pwd;
	private String token;
	private String email;
	private String mobile;
	private String name;
	private String role;
	private String branch;
	private String inst;
	private String[] domain;
	private String level;
	private String stream;
	private String type;
	private String topic;
	private String resume;
	private String job_description;
	private List<QA> res;
	
	
	
	 @Data
		public static class QA {
		    private String question;
		    private String answer;
		}
	
	
	
}
