package com.exam.Repositry;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.exam.Entity.MasUser;
import com.exam.Entity.MasUserToken;

@Repository
public class AuthRepo {

	@Autowired
	private MongoTemplate mongoTemplate;

	// LOGIN equivalent
	public List<MasUser> loginRepo(String uuid, String pwd) {
		Query q = Query.query(Criteria.where("uuid").is(uuid).and("user_pwd").is(pwd).and("active_flag").is("Y"));

		System.err.println("---" + q + "--" + mongoTemplate.find(q, MasUser.class));
		return mongoTemplate.find(q, MasUser.class);
	}

	// INSERT TOKEN
	public MasUserToken insertTokenRepo(String uuid, String jwt) {
		MasUserToken token = new MasUserToken();
		token.setUuid(uuid);
		token.setJwt(jwt);
		token.setIsInvalid(false);
		token.setIsLogout(false);
		token.setEntryTs(Instant.now());
		return mongoTemplate.insert(token, "mas_user_vs_token");
	}

	// TOKEN CHECK
	public List<MasUserToken> tokenCheckRepo(String uuid, String jwt) {
		Query q = Query.query(Criteria.where("uuid").is(uuid).and("jwt").is(jwt).and("is_invalid").is(false)
				.and("is_logout").is(false));
		return mongoTemplate.find(q, MasUserToken.class, "mas_user_vs_token");
	}

	// UPDATE token record (Refresh or Logout)
	public long updateTokenRecordRepo(String type, String jwt) {
		Query q = Query.query(Criteria.where("jwt").is(jwt));
		Update u = new Update();
		if ("Refresh".equalsIgnoreCase(type)) {
			u.set("is_invalid", true);
		} else if ("Logout".equalsIgnoreCase(type)) {
			u.set("is_logout", true);
		} else {
			return 0;
		}
		return mongoTemplate.updateMulti(q, u, MasUserToken.class, "mas_user_vs_token").getModifiedCount();
	}

}
