package com.evlink.domain.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evlink.domain.login.dao.PasswordLessLoginDao;
import com.evlink.domain.login.vo.PasswordLessUserInfoVO;

@Service
public class PasswordLessLoginService {
	
	@Autowired
	private PasswordLessLoginDao plLoginDao;
	
	// Login Check
	public PasswordLessUserInfoVO checkPassword(PasswordLessUserInfoVO userinfo) {
		return plLoginDao.checkPassword(userinfo);
	}
    
    // Search for User Information
	public PasswordLessUserInfoVO getUserInfo(PasswordLessUserInfoVO userinfo) {
		return plLoginDao.getUserInfo(userinfo);
	}
    
    // Password Update
    public void updatePassword(PasswordLessUserInfoVO userinfo) {
    	plLoginDao.updatePassword(userinfo);
    }
    
    // User Registration
    public void createUserInfo(PasswordLessUserInfoVO userinfo) {
    	plLoginDao.createUserInfo(userinfo);
    }
    
    // User Deletion
    public void withdrawUserInfo(PasswordLessUserInfoVO userinfo) {
    	plLoginDao.withdrawUserInfo(userinfo);
    }
    
    // Password Change
    public void changepw(PasswordLessUserInfoVO userinfo) {
    	plLoginDao.changepw(userinfo);
    }
}
