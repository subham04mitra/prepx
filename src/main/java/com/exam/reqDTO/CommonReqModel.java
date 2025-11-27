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
	private String paperid;
	private String paper_id;
	private String user_pwd;
	private String token;
	private Object subject;
	private Object chapter;
	private String examName;
	private int duration;
	private String examDate;
	private int totalMarks;
	private int totalQs;
	private int eachMark;
	private String exam;
	private String examType;
	private Object topics;
	private String id;
	private String pwd;
	private String email;
	private String mobile;
	private String name;
	private String role;
	private String branch;
	private String inst;
	private String owner;
	private String[] domain;
	private String level;
	 private List<QA> res;
	
	
	
	 @Data
		public static class QA {
		    private String question;
		    private String answer;
		}
	
	
	
}
