package com.exam.Repositry;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.AptiFeedback;

public interface AptiFeedbackRepository extends MongoRepository<AptiFeedback, String>{

	List<AptiFeedback> findByUuid(String uuid);
	
}
