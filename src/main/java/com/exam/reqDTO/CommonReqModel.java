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
	private int count;
	private String user_pwd;
	private String token;
	private String email;
	private String mobile;
	private String name;
	private String role;
	private String branch;
	private String content;
	private String inst;
	private String[] domain;
	private String level;
	private String stream;
	private String type;
	private String topic;
	private String resume;
	private int rating;
	private String googleToken;
	private int otp;
	private String feedback;
	private String ref;
	private String answer;
	private long qsId;
	private String job_description;
	private List<QA> res;
	private List<String> improvements;
	private String profilePic;
	 private String firstName;
	    private String lastName;
	    private String headline;
	    private String phone;
	    private String city;
	    private String country;
	    private String templateId;
	    private String summary;
	    private String slug;
	    private String skills;

	    private List<Social> socials;
	    private List<Experience> experience;
	    private List<Education> education;
	    private List<Project> projects;
	    
	    private List<Language> languages;
	    private List<Certificate> certifications;
	    private List<Achievement> achievements;

	    
	    
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
	    public static class Language {
	        private String proficiency;
	        private String name;
	    }
	    
	    
	    @Data
	    @NoArgsConstructor
	    @AllArgsConstructor
	    public static class Certificate {
	        private String credentialUrl;
	        private String name;
	        private String issueDate;
	        private String issuingOrganization;
	    }
	    
	    @Data
	    @NoArgsConstructor
	    @AllArgsConstructor
	    public static class Achievement {
	        private String description;
	        private String title;
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
