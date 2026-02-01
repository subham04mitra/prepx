package com.exam.Entity;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.exam.Entity.UserProfile.Social;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "user_vs_sub")
public class UserSubscription {

	@Id
	public String id;

	@Field("uuid")
	private String uuid;

	@Field("sub_type")
	private String subType;

	@Field("count")
	private int count;

	@Field("coin")
	private int coin;

	@Field("tcount")
	private int tCount;

	@Field("rcount")
	private int rCount;

	@Field("entry_ts")
	private Instant entryTs;

}
