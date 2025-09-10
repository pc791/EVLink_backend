package com.evlink.domain.reservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evlink.domain.charger.dao.ChargerDao;
import com.evlink.domain.charger.vo.ChargerVO;
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
		// 충전소의 예약 가능 여부(res_yn) 확인
		String resYn = chargerDao.getResYn(vo.getChargerId());
		if (resYn == null || !"Y".equals(resYn)) {
			throw new IllegalStateException("예약이 불가능한 충전소입니다.");
		}
		// 예약 시간 중복 확인
		int conflictCount = reservationDao.checkReservationTime(vo);
		if (conflictCount > 0) {
			// 중복되는 예약이 있다면 예외 발생
			throw new IllegalStateException("해당 시간에 이미 예약이 존재합니다.");
		}
		// 모든 검증 통과 후, 예약 등록 진행
        int result = reservationDao.reservation(vo);
		// 예약 등록이 성공적으로 완료되면 이메일 알림 전송
        if (result > 0) {
            String toEmail = vo.getResEmail();
            // ChargerDao를 통해 ChargerVO 객체를 조회
            ChargerVO chargerVO = chargerDao.getChargerById(vo.getChargerId());
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
}
