package com.exam.Repositry;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.UserProfile;

public interface UserProfileRepository extends MongoRepository<UserProfile, String>{

	Optional<UserProfile> findByUuid(String uuid);
}
