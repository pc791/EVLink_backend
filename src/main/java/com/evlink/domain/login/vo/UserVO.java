package com.evlink.domain.login.vo;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("usrvo")
public class UserVO {
	private int user_id;
	private String user_tp;
	private String join_type;
	private String login_id;
	private String login_pw;
	private String user_nm;
	private String user_phone;
	private String user_nicknm;
	private int login_fail_cnt;
	private String access_token;
	private String refresh_token; 
}
