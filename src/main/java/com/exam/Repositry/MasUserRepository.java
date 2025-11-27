package com.exam.Repositry;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.MasUser;

public interface MasUserRepository extends MongoRepository<MasUser, String> {
    Optional<MasUser> findByUuidAndUserPwdAndActiveFlag(String uuid, String userPwd, String activeFlag);
    // or
    Optional<MasUser> findByUuidAndActiveFlag(String uuid, String activeFlag);
}
