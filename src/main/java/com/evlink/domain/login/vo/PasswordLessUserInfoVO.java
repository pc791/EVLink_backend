package com.evlink.domain.login.vo;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("plUserInfo")
public class PasswordLessUserInfoVO {
	private String login_id;
	private String login_pw;
	private String join_type;
	private String user_tp;
	private Date reg_dt;
}
