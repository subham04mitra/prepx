package com.exam.Repositry;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.UserFeedback;

public interface UserFeedbackRepository extends MongoRepository<UserFeedback,String>{

}
