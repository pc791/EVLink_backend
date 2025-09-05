package com.evlink.domain.login.dao;

import org.apache.ibatis.annotations.Mapper;

import com.evlink.domain.login.vo.PasswordLessUserInfoVO;

@Mapper
public interface PasswordLessLoginDao {
	// Login Check
	PasswordLessUserInfoVO checkPassword(PasswordLessUserInfoVO userinfo);
    
    // Search for User Information
	PasswordLessUserInfoVO getUserInfo(PasswordLessUserInfoVO userinfo);
    
    // Password Update
    void updatePassword(PasswordLessUserInfoVO userinfo);
    
    // User Registration
    void createUserInfo(PasswordLessUserInfoVO userinfo);
    
    // User Deletion
    void withdrawUserInfo(PasswordLessUserInfoVO userinfo);
    
    // Password Change
    void changepw(PasswordLessUserInfoVO userinfo);
}
