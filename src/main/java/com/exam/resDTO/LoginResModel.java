package com.exam.resDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResModel {
	
	private String user_name;
	private String user_role;
	private String user_email;
	private String user_mobile;
	private String user_inst;
	private String user_branch;
	private String jwt_token;
}
