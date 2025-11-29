package com.exam.Entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
@Data
@Document(collection = "interview_feedback")
public class InterviewFeedback {

    @Id
    private String id;

    private String uuid;
    private Instant entry_ts;
    private String topics;
    // Scores
    private double technicalScore;
    private double communicationScore;
    private double voiceClarityScore;
    private double overallScore;

    // Lists stored as arrays in MongoDB
    private List<String> strengths;
    private List<String> improvements;

    private String verdict;

    // getters & setters
}

