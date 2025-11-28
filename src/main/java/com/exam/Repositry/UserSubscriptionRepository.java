package com.exam.Repositry;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.UserSubscription;

public interface UserSubscriptionRepository extends MongoRepository<UserSubscription,String> {

	
	Optional<UserSubscription> findByUuid(String uuid);
}
