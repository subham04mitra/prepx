package com.exam.Repositry;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.UserSubmission;

public interface UserSubmissionRepository extends MongoRepository<UserSubmission, String> {

	List<UserSubmission> findByUuidAndDateBetween(String uuid, Instant start, Instant end);
}
