package com.evlink.domain.mypage.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evlink.domain.mypage.dao.ChargerDao;
import com.evlink.domain.mypage.vo.ChargerVO;
import com.evlink.global.service.GeocodingService;

@Service
public class ChargerService {
	@Autowired
	private ChargerDao chargerDao;
	@Autowired
	private GeocodingService geocodingService;

//	@Autowired
//	private UserDao userDao; // user_tp 업데이트

	@Transactional
	public void addCharger(ChargerVO vo) {
		// 1. 충전기 중복등록 확인
		int conflictCount = chargerDao.checkChargerDuplicate(vo);

		if (conflictCount > 0) {
			// 한아이디로 충전기를 중복등록 한다면 예외 발생
			throw new IllegalStateException("이미 충전기를 등록했습니다.");
		}
		// 2. 주소(addr)를 이용하여 위도(lat), 경도(lng) 자동 설정
		try {
			String address = vo.getAddr();
			String addrDetail = vo.getAddrDetail();
			// 상세 주소가 있으면 일반 주소에 추가하여 결합
	        if (address != null && !address.trim().isEmpty()) {
	            String fullAddress = address;
	            if (addrDetail != null && !addrDetail.trim().isEmpty()) {
	                fullAddress += " " + addrDetail; // 공백을 추가하여 결합
	            }
	            
	            double[] coords = geocodingService.getCoordinatesFromAddress(fullAddress);
	            System.out.println("fullAddress = " + fullAddress);
	            vo.setLatitude(coords[0]); // 위도 설정
	            vo.setLongitude(coords[1]); // 경도 설정
	        }
		} catch (Exception e) {
			// 주소 변환 실패 시 예외 처리
			e.printStackTrace();
			throw new IllegalStateException("주소 변환에 실패했습니다. 유효한 주소를 입력해주세요.");
		}
		// '050' + 8자리 난수 생성 로직
		Random random = new Random();
		int randomNumber = 10000000 + random.nextInt(90000000);
		String safeChargerTel = "050" + randomNumber;

		// 생성된 값을 VO 객체에 설정
		vo.setSafeChargerTel(safeChargerTel);
		chargerDao.addCharger(vo);
		chargerDao.callUserTpUpdate(vo);
		
	}

	@Transactional
	public int updateCharger(ChargerVO vo) {
		try {
			String address = vo.getAddr();
			String addrDetail = vo.getAddrDetail();
			// 상세 주소가 있으면 일반 주소에 추가하여 결합
	        if (address != null && !address.trim().isEmpty()) {
	            String fullAddress = address;
	            if (addrDetail != null && !addrDetail.trim().isEmpty()) {
	                fullAddress += " " + addrDetail; // 공백을 추가하여 결합
	            }
	            
	            double[] coords = geocodingService.getCoordinatesFromAddress(fullAddress);
	            System.out.println("fullAddress = " + fullAddress);
	            vo.setLatitude(coords[0]); // 위도 설정
	            vo.setLongitude(coords[1]); // 경도 설정
	        }
		} catch (Exception e) {
			// 주소 변환 실패 시 예외 처리
			e.printStackTrace();
			throw new IllegalStateException("주소 변환에 실패했습니다. 유효한 주소를 입력해주세요.");
		}
		int resultValue = chargerDao.updateCharger(vo);
		chargerDao.callUserTpUpdate(vo);
		return resultValue;
	}

	public int deleteCharger(long chargerId) {
		return chargerDao.deleteCharger(chargerId);
	}
	
	public List<ChargerVO> getAllChargers() {
        return chargerDao.getAllChargers();
    }
	
	public ChargerVO getCharger(long userId) {
		return chargerDao.getCharger(userId);
	}
}
