package com.evlink.domain.mypage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evlink.domain.mypage.dao.CommDao;
import com.evlink.domain.mypage.vo.CommVO;

@Service
public class CommService {
	@Autowired
	private CommDao commDao;
	
	// 공통사항 조회
	public CommVO getUserComm(int user_id) {
		return commDao.getUserComm(user_id);
	}
	
	// 공통사항 저장
	@Transactional
	public int setUserComm(CommVO vo) {
		int resultValue = commDao.setUserComm(vo);
		commDao.callUserTpUpdate(vo);
		return resultValue;
	}
}
