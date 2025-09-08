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
	private String id;
	private String pw;
	private String email;
	private Date regdate;
}
