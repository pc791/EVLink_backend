package com.evlink.domain.reservation.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.evlink.domain.reservation.vo.ReservationVO;


@Mapper
public interface ReservationDao {
	//예약시간 중복체크
	int checkReservationTime(ReservationVO vo);
	
	// 예약
	int reservation(ReservationVO vo);
	
	// 취소
	int reservationCancel(long resId);
	
	// 예약 상태 조회
	String getResStatus(@Param("resId") long resId);	
}
