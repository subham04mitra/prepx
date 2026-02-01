package com.exam.Repositry;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.MasSubscription;

public interface MasSubscriptionRepository extends MongoRepository<MasSubscription, String> {

	Optional<MasSubscription> findBySubType(String subType);

}
