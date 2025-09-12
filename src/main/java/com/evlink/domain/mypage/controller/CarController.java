/**
 * 2025.09.10
 * jeong yeon ho
 * 마이페이지 차량정보
 */
package com.evlink.domain.mypage.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evlink.domain.mypage.service.CarService;
import com.evlink.domain.mypage.vo.CarVO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/mypage/car")
public class CarController {

	@Autowired
	private CarService carService;

	// 차량등록
	@PostMapping("/add")
	public ResponseEntity<Map<String, Object>> addCharger(@RequestBody CarVO vo) {
		Map<String, Object> response = new HashMap<>();

		// System.out.println("Received VO : " + vo.toString());
		// System.out.println("User ID : " + vo.getUser_id());
		try {
			
			carService.addCar(vo);

			response.put("success", true);
			response.put("message", "차량등록 성공");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "차량 등록 중 오류가 발생했습니다: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// 등록차량 수정
	@PutMapping("/{car_id}")
	public ResponseEntity<Map<String, Object>> updateCharger(@PathVariable("car_id") int car_id, @RequestBody CarVO vo) {
		Map<String, Object> response = new HashMap<>();

		vo.setCar_id(car_id);

		int updatedRows = carService.updateCar(vo);

		if (updatedRows > 0) {
			response.put("success", true);
			response.put("message", "차량정보를 수정하였습니다.");
			return ResponseEntity.ok(response);
		} else {
			response.put("success", false);
			response.put("message", "차량 정보가 존재하지 않습니다.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
	
	// 등록차량 삭제
		@DeleteMapping("/{car_id}")
		public ResponseEntity<Map<String, Object>> deleteCharger(@PathVariable("car_id") int car_id) {
			Map<String, Object> response = new HashMap<>();

			int deletedRows = carService.deleteCar(car_id);

			if (deletedRows > 0) {
				response.put("success", true);
				response.put("message", "차량정보 삭제 성공!");
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "삭제하려는 차량정보가 존재하지 않습니다.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		}

	// 등록차량 조회
	@GetMapping("/{user_id}")
	public CarVO getUserCommInfo(HttpSession session, @PathVariable("user_id") int user_id) {
		return carService.getCar(user_id);
	}
	
}