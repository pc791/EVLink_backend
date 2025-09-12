package com.evlink.domain.mypage.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.evlink.domain.mypage.vo.CommVO;

@Mapper
public interface CommDao {
	// 공통사항 조회
	CommVO getUserComm(int user_id);
	// 공통사항 저장
	int setUserComm(CommVO vo);
	// 프로시저 호출
	void callUserTpUpdate(CommVO vo);
}
