package com.exam.Entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "apti_feedback")
public class AptiFeedback {

	@Id
	private String id;
	
	private String uuid;
	private BigDecimal totalMarksObtained;
	private BigDecimal negativeMarks;
	private int wrong;
	private int right;
	private int skipped;
	private int attempted;
	private int totalQuestions;
	
	private List<QuestionAttemptDetail> attemptDetails;
	
	@Data
    public static class QuestionAttemptDetail {
        private long qsId;
        private String question;       
        private String userAnswer;
        private String correctAnswer;
        private String status;         
    }
}
