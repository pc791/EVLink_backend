package com.evlink.domain.login.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.evlink.domain.login.vo.UserVO;

@Mapper
public interface AuthDao {
	// 회원가입여부 확인
	int checkSignUp(Map<String, String> map);
	
	// SNS 회원가입
	int signUp(Map<String, String> map);
	
	// 회원조회
	UserVO getUserInfo(Map<String, String> map);
}
