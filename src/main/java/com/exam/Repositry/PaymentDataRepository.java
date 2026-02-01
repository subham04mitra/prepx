package com.exam.Repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.MasUser;
import com.exam.Entity.PaymentData;

public interface PaymentDataRepository extends MongoRepository<PaymentData, String> {

	List<PaymentData> findByUuid(String uuid);

}
