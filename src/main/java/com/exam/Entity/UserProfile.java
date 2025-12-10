package com.exam.Entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_profile")
public class UserProfile {

    @Id
    private String id;

    private String uuid;

    private String firstName;
    private String lastName;
    private String headline;
    private String email;
    private String mobile;
    private String city;
    private String country;
    private String templateId;
    private String url;
    private String summary;
    private String skills;

    private List<Social> socials;
    private List<Experience> experience;
    private List<Education> education;
    private List<Project> projects;

    private String resume;
    private String profilePic;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Social {
        private String network;
        private String url;
    };

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
}
