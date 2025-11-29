package com.exam.Repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.InterviewFeedback;

public interface InterviewFeedbackRepository extends MongoRepository<InterviewFeedback, String> {

	List<InterviewFeedback> findByUuid(String uuid);
	
}
