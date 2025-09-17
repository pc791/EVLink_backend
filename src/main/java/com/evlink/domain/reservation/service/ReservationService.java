package com.evlink.domain.reservation.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evlink.domain.mypage.dao.ChargerDao;
import com.evlink.domain.mypage.vo.ChargerVO;
import com.evlink.domain.reservation.dao.ReservationDao;
import com.evlink.domain.reservation.vo.ReservationVO;



@Service
public class ReservationService {
	@Autowired
	private ChargerDao chargerDao;
	@Autowired
	private ReservationDao reservationDao;
	@Autowired
	private MailService mailService;

	public int reservation(ReservationVO vo) {
		// 예약을 위해 ChargerVO 정보 조회
        ChargerVO chargerVO = chargerDao.getChargerById(vo.getChargerId());
        
        //충전소 정보 조회
        if (chargerVO == null) {
            throw new IllegalStateException("충전소 정보를 찾을 수 없습니다.");
        }
        
		// 충전소의 예약 가능 여부(res_yn) 확인
		String resYn = chargerVO.getResYn();
		if (!"Y".equals(resYn)) {
			throw new IllegalStateException("예약이 불가능한 충전소입니다.");
		}		
        
        // 충전소 운영 시간과 예약 시간 비교
        LocalTime openTime = chargerVO.getOpenTime();
        LocalTime closeTime = chargerVO.getCloseTime();
        LocalTime resStartTime = vo.getResStartTime();
        LocalTime resEndTime = vo.getResEndTime();

        // 예약 시작 시간이 운영 시간 시작보다 빠르거나
        // 예약 종료 시간이 운영 시간 종료보다 늦으면 예외 발생
        if (resStartTime.isBefore(openTime) || resEndTime.isAfter(closeTime)) {
            throw new IllegalStateException("예약 시간이 충전소 운영 시간을 벗어났습니다.");
        }
		
		// 예약 시간 중복 확인
		int conflictCount = reservationDao.checkReservationTime(vo);
		if (conflictCount > 0) {
			// 중복되는 예약이 있다면 예외 발생
			throw new IllegalStateException("해당 시간에 이미 예약이 존재합니다.");
		}		     

        // 시간당 요금(payHour)과 예약 시간 계산
        String payTotalStr = chargerVO.getPayTotal();

        try {
            int payTotal = Integer.parseInt(payTotalStr.replace(",", "").trim());
            long hours = Duration.between(resStartTime, resEndTime).toHours();
            if (hours <= 0) {
                // 예약 종료 시간이 시작 시간보다 이전일 경우를 처리 (예: 자정을 넘어가는 예약)
                hours = 24;
            }
            long payTotalHour = payTotal * hours;
            
            // 계산된 총 금액을 ReservationVO에 설정
            vo.setPayTotalHour(String.valueOf(payTotalHour));
        
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("시간당 요금 형식이 올바르지 않습니다.");
        }

		// 모든 검증 통과 후, 예약 등록 진행
        int result = reservationDao.reservation(vo);
		// 예약 등록이 성공적으로 완료되면 이메일 알림 전송
        if (result > 0) {
            String toEmail = vo.getResEmail();           
            try {
                mailService.sendEmail(toEmail, vo, chargerVO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
        return result;
	}

	public int reservationCancel(long resId) {
		// 1. 예약 상태 확인
		String resStatus = reservationDao.getResStatus(resId);

		if ("C".equals(resStatus)) {
			throw new IllegalStateException("이미 취소된 예약입니다.");
		}
		return reservationDao.reservationCancel(resId);
	}
	
	// customer/provider select list
	public List<Map<String, Object>> reservationList(Map<String, String> map) {
		return reservationDao.reservationList(map);
	}
	
	public int totalCount(Map<String, String> map) {
		return reservationDao.totalCount(map);
	}
	/**
     * 특정 충전소의 특정 날짜에 대해 예약된 시간 목록을 반환합니다.
     * 이 시간 목록은 프론트엔드에서 예약 불가능한 시간대를 표시하는 데 사용됩니다.
     * @param chargerId 충전소 ID
     * @param date      조회 날짜 (yyyy-MM-dd 형식)
     * @return 예약된 시간(시 단위) 목록 (예: [9, 10, 11])
     */
	public List<Integer> getReservedTimes(Long chargerId, String date) {
	    // 충전소 정보 조회
	    ChargerVO chargerVO = chargerDao.getChargerById(chargerId);
	    if (chargerVO == null) {
	        throw new IllegalStateException("충전소 정보를 찾을 수 없습니다.");
	    }
	    
	    LocalTime openTime = chargerVO.getOpenTime();
	    LocalTime closeTime = chargerVO.getCloseTime();
	    
	    // 예약된 시간 범위 목록을 가져옵니다.
	    List<Map<String, Object>> reservedRanges = reservationDao.getReservedTimes(chargerId, date);

	    // 예약된 시간(시 단위) 목록을 생성합니다.
	    List<Integer> reservedHours = new ArrayList<>();
	    
	    if (reservedRanges != null && !reservedRanges.isEmpty()) {
	        for (Map<String, Object> range : reservedRanges) {
	            if (range == null) {
	                continue;
	            }
	            
	            Object startTimeObj = range.get("res_start_time");
	            Object endTimeObj = range.get("res_end_time");

	            if (startTimeObj == null || endTimeObj == null) {
	                continue;
	            }

	            LocalTime startTime;
	            LocalTime endTime;
	            
	            if (startTimeObj instanceof java.sql.Time) {
	                startTime = ((java.sql.Time) startTimeObj).toLocalTime();
	            } else {
	                startTime = (LocalTime) startTimeObj;
	            }
	            
	            if (endTimeObj instanceof java.sql.Time) {
	                endTime = ((java.sql.Time) endTimeObj).toLocalTime();
	            } else {
	                endTime = (LocalTime) endTimeObj;
	            }

	            while (startTime.isBefore(endTime)) {
	                reservedHours.add(startTime.getHour());
	                startTime = startTime.plusHours(1);
	            }
	        }
	    }

	    // 최종적으로 비활성화할 시간 목록을 계산합니다.
	    List<Integer> finalDisabledHours = new ArrayList<>();
	    
	    // 0시부터 23시까지 모든 시간을 반복합니다.
	    for (int i = 0; i < 24; i++) {
	        LocalTime checkTime = LocalTime.of(i, 0);

	        // 충전소 운영 시간을 벗어나는지 확인
	        if (checkTime.isBefore(openTime) || checkTime.isAfter(closeTime.minusHours(1))) {
	            finalDisabledHours.add(i);
	        }
	        
	        // 예약된 시간인지 확인
	        else if (reservedHours.contains(i)) {
	            finalDisabledHours.add(i);
	        }
	    }	    
	    return finalDisabledHours; // 비활성화할 시간 목록을 반환합니다.
	}

}

