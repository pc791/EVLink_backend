package com.evlink.domain.charger.controller;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evlink.domain.charger.service.ChargerService;
import com.evlink.domain.charger.vo.ChargerVO;

@RestController
@RequestMapping("/api/charger")
public class ChargerController {

	@Autowired
	private ChargerService chargerService;

	// 개인 충전소 등록
	@PostMapping("/add")
	public ResponseEntity<Map<String, Object>> addCharger(@RequestBody ChargerVO vo) {
		Map<String, Object> response = new HashMap<>();

		System.out.println("Received VO : " + vo.toString());
		System.out.println("User ID : " + vo.getUserId());
		try {
			// 시작 시간이 종료 시간보다 늦은지 검증
			LocalTime openTime = vo.getOpenTime();
			LocalTime closeTime = vo.getCloseTime();

			if (openTime.isAfter(closeTime) || openTime.equals(closeTime)) {
				response.put("success", false);
				response.put("message", "오류: 종료 시간이 시작 시간보다 같거나 빠를 수 없습니다.");
				return ResponseEntity.badRequest().body(response);
			}

			// ChargerService.addCharger() 메서드는 반환값이 없으므로 성공 시 메시지 반환
			chargerService.addCharger(vo);

			response.put("success", true);
			response.put("message", "개인 충전소 등록 성공!");
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			response.put("success", false);
			response.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (IllegalStateException e) {
			response.put("success", false);
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "충전소 등록 중 오류가 발생했습니다: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// 개인 충전소 정보 수정
	// URL: PUT http://localhost:80/evlink/charger/{chargerId}
	@PutMapping("/{chargerId}")
	public ResponseEntity<Map<String, Object>> updateCharger(@PathVariable("chargerId") long chargerId, @RequestBody ChargerVO vo) {
		Map<String, Object> response = new HashMap<>();

		vo.setChargerId(chargerId);

		int updatedRows = chargerService.updateCharger(vo);

		if (updatedRows > 0) {
			response.put("success", true);
			response.put("message", "개인 충전소 정보 수정 성공!");
			return ResponseEntity.ok(response);
		} else {
			response.put("success", false);
			response.put("message", "수정하려는 충전소 정보가 존재하지 않습니다.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// 개인 충전소 정보 삭제
	@DeleteMapping("/{chargerId}")
	public ResponseEntity<Map<String, Object>> deleteCharger(@PathVariable("chargerId") long chargerId) {
		Map<String, Object> response = new HashMap<>();

		int deletedRows = chargerService.deleteCharger(chargerId);

		if (deletedRows > 0) {
			response.put("success", true);
			response.put("message", "개인 충전소 정보 삭제 성공!");
			return ResponseEntity.ok(response);
		} else {
			response.put("success", false);
			response.put("message", "삭제하려는 충전소 정보가 존재하지 않습니다.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
}