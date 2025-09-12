package com.evlink.domain.mypage.dao;

import org.apache.ibatis.annotations.Mapper;

import com.evlink.domain.mypage.vo.CarVO;

@Mapper
public interface CarDao {
	// 차량등록여부 확인
	int checkCarDuplicate(int user_id);
	
	// 차량정보 조회
	CarVO getCar(int user_id);
	
	// 차량등록
	void addCar(CarVO vo);
	
	// 차량정보 수정
	int updateCar(CarVO vo);

	// 차량정보 삭제
	int deleteCar(int car_id);
	
	// 프로시저 호출
	void callUserTpUpdate(CarVO vo);
}
