package com.evlink.domain.charger.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.evlink.domain.charger.vo.ChargerVO;

@Mapper
public interface ChargerDao {
	// 충전기 등록 중복체크
	int checkChargerDuplicate(ChargerVO vo);

	// 충전소 등록
	void addCharger(ChargerVO vo);

	// 충전소 상태 수정
	int updateCharger(ChargerVO vo);

	// 충전소 정보 삭제
	int deleteCharger(Long chargerId);
	
	//예약시 예약가능여부 조회
	String getResYn(@Param("chargerId") long chargerId);
	
	//메일 알림시 충전소 조회
	ChargerVO getChargerById(@Param("chargerId") long chargerId);
}
