package com.exam.Repositry;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.MasUser;
import com.exam.Entity.OTP;

public interface OTPRepository extends MongoRepository<OTP, String>{

	  Optional<OTP> findByUuidAndEmailOtp(String uuid,int emailOtp);
	
}
