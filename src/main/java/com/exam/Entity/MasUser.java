package com.exam.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

import java.time.Instant;

@Data
@Document(collection = "mas_user")
public class MasUser {

	@Id
	private String id;

	@Field("uuid")
	private String uuid;

	@Field("user_pwd")
	private Object userPwd;

	@Field("user_name")
	private String userName;

	@Field("user_role")
	private String userRole;

	@Field("desg")
	private String userInst;

	@Field("user_branch")
	private String userBranch;

	@Field("active_flag")
	private String activeFlag;

	@Field("entry_ts")
	private Instant entryTs;

	@Field("user_email")
	private String userEmail;

	@Field("ref_code")
	private String refCode;

	@Field("user_mobile")
	private String userMobile;

	@Field("exp")
	private String stream;

	@Field("is_profile_complete")
	private boolean complete;

	@Field("email_otp")
	private int emailOtp;

}
