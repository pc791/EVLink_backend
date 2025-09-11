package com.evlink.domain.mypage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evlink.domain.mypage.dao.CarDao;
import com.evlink.domain.mypage.vo.CarVO;

@Service
public class CarService {
	@Autowired
	private CarDao carDao;
			
	// 차량정보 조회
	public CarVO getCar(int user_id) {
		return carDao.getCar(user_id);
	}
		
	// 차량등록
	public void addCar(CarVO vo) {
		int count = carDao.checkCarDuplicate(vo.getUser_id());
		
		if(count > 0) {
			throw new IllegalStateException("이미 충전기를 등록했습니다.");
		} 
		
		carDao.addCar(vo);
	}
		
	// 차량정보 수정
	public int updateCar(CarVO vo) {
		return carDao.updateCar(vo);
	}

	// 차량정보 삭제
	public int deleteCar(int car_id) {
		return carDao.deleteCar(car_id);
	}
}
