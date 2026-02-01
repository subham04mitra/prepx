package com.exam.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

import java.time.Instant;

@Data
@Document(collection = "mas_user_vs_token")
public class MasUserToken {

	@Id
	private String id;

	@Field("uuid")
	private String uuid;

	@Field("jwt")
	private String jwt;

	@Field("is_invalid")
	private Boolean isInvalid;

	@Field("is_logout")
	private Boolean isLogout;

	@Field("entry_ts")
	private Instant entryTs;
}
