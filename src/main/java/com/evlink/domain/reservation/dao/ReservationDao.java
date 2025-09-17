package com.evlink.domain.reservation.dao;

import java.util.List;
import java.util.Map;

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
	
	// customer/provider select list
	List<Map<String, Object>> reservationList(Map<String, String> map);

	int totalCount(Map<String, String> map);
	
	// 예약된 시간 목록을 가져오는 메서드 추가
	List<Map<String, Object>> getReservedTimes(@Param("chargerId") Long chargerId, @Param("date") String date);
}
