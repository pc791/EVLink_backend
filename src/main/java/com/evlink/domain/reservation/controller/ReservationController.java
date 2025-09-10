package com.evlink.domain.reservation.controller;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evlink.domain.reservation.service.ReservationService;
import com.evlink.domain.reservation.vo.ReservationVO;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
	@Autowired
	private ReservationService reservationService;

	// 예약
	@PostMapping("")
	public ResponseEntity<Map<String, Object>> reservation(@RequestBody ReservationVO vo) {
		Map<String, Object> response = new HashMap<>();

		System.out.println("Received VO object: " + vo.toString());

		if (vo.getUserId() == null) {
			response.put("success", false);
			response.put("message", "오류: userId가 누락되었습니다.");
			return ResponseEntity.badRequest().body(response);
		}
		if (vo.getChargerId() == null) {
			response.put("success", false);
			response.put("message", "오류: 충전소 ID가 누락되었습니다.");
			return ResponseEntity.badRequest().body(response);
		}

		try {
			LocalTime startTime = vo.getResStartTime();
			LocalTime endTime = vo.getResEndTime();

			if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
				response.put("success", false);
				response.put("message", "오류: 종료 시간이 시작 시간보다 같거나 빠를 수 없습니다.");
				return ResponseEntity.badRequest().body(response);
			}

			// reservationService.reservation() 메서드가 int를 반환하도록 수정
			int result = reservationService.reservation(vo);
			if (result > 0) {
				response.put("success", true);
				response.put("message", "예약 성공!");
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "예약에 실패했습니다. (알 수 없는 오류)");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}

		} catch (IllegalStateException e) {
			response.put("success", false);
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "예약 중 오류가 발생했습니다: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// 예약 취소
		@PutMapping("/cancel/{resId}")
		public ResponseEntity<Map<String, Object>> reservationCancel(@PathVariable("resId") long resId) {
			Map<String, Object> response = new HashMap<>();

			try {
				int updatedRows = reservationService.reservationCancel(resId);

				if (updatedRows > 0) {
					response.put("success", true);
					response.put("message", "예약 취소 성공!");
					return ResponseEntity.ok(response);
				} else {
					response.put("success", false);
					response.put("message", "취소하려는 예약 정보가 존재하지 않습니다.");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
				}
			} catch (IllegalStateException e) {
				response.put("success", false);
				response.put("message", e.getMessage());
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} catch (Exception e) {
				response.put("success", false);
				response.put("message", "예약 취소 중 오류가 발생했습니다.");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
		}
}