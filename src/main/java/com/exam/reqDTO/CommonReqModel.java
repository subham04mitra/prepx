package com.exam.reqDTO;

import java.util.List;

import com.exam.Entity.UserProfile;
import com.exam.Entity.UserProfile.Education;
import com.exam.Entity.UserProfile.Experience;
import com.exam.Entity.UserProfile.Project;
import com.exam.Entity.UserProfile.Social;
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
	private int rating;
	private String feedback;
	private String ref;
	private String answer;
	private long qsId;
	private String job_description;
	private List<QA> res;
	private String profilePic;
	 private String firstName;
	    private String lastName;
	    private String headline;
	    private String phone;
	    private String city;
	    private String country;

	    private String summary;
	    private String skills;

	    private List<Social> socials;
	    private List<Experience> experience;
	    private List<Education> education;
	    private List<Project> projects;

	    
	    
	    @Data
	    @NoArgsConstructor
	    @AllArgsConstructor
	    public static class Social {
	        private String network;
	        private String url;
	    }

	    
	    @Data
	    @NoArgsConstructor
	    @AllArgsConstructor
	    public static class Experience {
	        private String companyName;
	        private String jobTitle;
	        private String startDate;
	        private String endDate;
	        private String description;
	    }

	    @Data
	    @NoArgsConstructor
	    @AllArgsConstructor
	    public static class Education {
	        private String institutionName;
	        private String degree;
	        private String fieldOfStudy;
	        private String startDate;
	        private String endDate;
	        private String score;
	    }

	    @Data
	    @NoArgsConstructor
	    @AllArgsConstructor
	    public static class Project {
	        private String title;
	        private String techStack;
	        private String projectUrl;
	        private String repoUrl;
	        private String description;
	    }

	
	
	 @Data
		public static class QA {
		    private String question;
		    private String answer;
		}
	
	
	
}
