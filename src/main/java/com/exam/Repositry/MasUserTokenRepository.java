package com.exam.Repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.MasUserToken;

public interface MasUserTokenRepository extends MongoRepository<MasUserToken, String> {
    List<MasUserToken> findByUuidAndJwtAndIsInvalidFalseAndIsLogoutFalse(String uuid, String jwt);
    int deleteByJwt(String jwt); // if you need deletion
    Optional<MasUserToken> findByJwt(String jwt);

}
