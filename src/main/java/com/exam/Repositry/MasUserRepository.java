package com.exam.Repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.MasUser;

public interface MasUserRepository extends MongoRepository<MasUser, String> {
	Optional<MasUser> findByUuidAndUserPwdAndActiveFlag(String uuid, String userPwd, String activeFlag);

	// or
	Optional<MasUser> findByRefCode(String refCode);

	Optional<MasUser> findByUserEmailAndActiveFlag(String userEmail, String activeFlag);

	Optional<MasUser> findByUuidAndActiveFlag(String uuid, String activeFlag);

	List<MasUser> findByUserEmailOrUserMobile(String userEmail, String userMobile);
}
