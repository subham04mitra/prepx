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
    private Object userPwd;    // Use Object to accept number or string

    @Field("user_name")
    private String userName;

    @Field("user_role")
    private String userRole;

    @Field("user_inst")
    private String userInst;

    @Field("user_branch")
    private String userBranch;

    @Field("active_flag")
    private String activeFlag;

    @Field("entry_ts")
    private Instant entryTs;

    @Field("user_email")
    private String userEmail;

    @Field("user_mobile")
    private String userMobile;

    @Field("entry_by")
    private String entryBy;

    @Field("owner_id")
    private String ownerId;

    @Field("admin_id")
    private String adminId;
}
