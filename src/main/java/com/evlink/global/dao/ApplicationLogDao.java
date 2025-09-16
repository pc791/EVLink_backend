package com.evlink.global.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.evlink.global.vo.ApplicationLoggerVO;


@Mapper
public interface ApplicationLogDao {
	@Insert("INSERT INTO tb_login_log(session_id, ip_addr, provider, login_id, user_id, user_tp, auth, req_dt) VALUES(#{session_id}, #{ip_addr}, #{provider}, #{login_id}, #{user_id}, #{user_tp}, #{auth}, sysdate())")
	public void addLogging(ApplicationLoggerVO vo); // Log 기록
	
}
