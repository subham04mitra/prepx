package com.exam.Repositry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public class AuthRepo {

	
	@Autowired
	NamedParameterJdbcTemplate jdbctemplate;
	
	
	
	public List<Map<String,Object>> loginRepo(String uuid,String pwd){
		List<Map<String,Object>> data=null;
		Map<String,Object> params=new HashMap<>();
		String query="";
		try {
			query="""
					SELECT x.user_name,x.user_role,x.user_inst,x.user_branch,x.user_mobile,x.user_email
					FROM masadmin.mas_user x
					where x."uuid" =:uuid and user_pwd =:pwd and active_flag ='Y'
					""";
			params.put("uuid", uuid);
			params.put("pwd", pwd);
			data=jdbctemplate.queryForList(query, params);
		}catch(Exception ex) {
			ex.printStackTrace();
				throw ex;
			}
		return data;
	}
	
	
	public int insertTokenRepo(String uuid,String jwt){
		int data=0;
		Map<String,Object> params=new HashMap<>();
		String query="";
		try {
//			System.out.println(uuid+jwt);
			query="""
					INSERT INTO masadmin.mas_user_vs_token
					("uuid", jwt,is_invalid,is_logout, entry_ts)
					VALUES(:uuid, :jwt,false,false, now());
					""";
			params.put("uuid", uuid);
			params.put("jwt", jwt);
			data=jdbctemplate.update(query, params);
		}catch(Exception ex) {
			ex.printStackTrace();
				throw ex;
			}
		return data;
	}
	
	public List<Map<String,Object>> tokenCheckRepo(String uuid,String jwt){
		List<Map<String,Object>> data=null;
		Map<String,Object> params=new HashMap<>();
		String query="";
		try {
			query="""
					SELECT is_invalid FROM masadmin.mas_user_vs_token 
					where "uuid" =:uuid and jwt=:jwt 
					and is_invalid =false and is_logout =false

					""";
			params.put("uuid", uuid);
			params.put("jwt", jwt);
			data=jdbctemplate.queryForList(query, params);
		}catch(Exception ex) {
				throw ex;
			}
		return data;
	}
	
	public int updateTokenRecordRepo(String type,String jwt){
		int data=0;
		Map<String,Object> params=new HashMap<>();
		String query="";
		try {
			if("Refresh".equalsIgnoreCase(type)) {
				query="""
						update masadmin.mas_user_vs_token set is_invalid =true 
						where jwt=:jwt 
						""";
			}
			else if("Logout".equalsIgnoreCase(type)) {
				query="""
						update masadmin.mas_user_vs_token set is_logout =true 
						where jwt=:jwt 
						""";
			}
			
			
			
			params.put("jwt", jwt);
			data=jdbctemplate.update(query, params);
		}catch(Exception ex) {
			ex.printStackTrace();
				throw ex;
			}
		return data;
	}
}
