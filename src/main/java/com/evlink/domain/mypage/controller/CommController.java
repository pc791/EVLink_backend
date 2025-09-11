/**
 * 2025.09.10
 * jeong yeon ho
 * 마이페이지 공통사항
 */
package com.evlink.domain.mypage.controller;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evlink.domain.mypage.service.CommService;
import com.evlink.domain.mypage.vo.CommVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/mypage/common")
public class CommController {
	@Autowired
	private CommService commService;
	
	// 공통사항 조회
	@GetMapping("/{user_id}")
	public CommVO getUserCommInfo(HttpSession session, @PathVariable("user_id") int user_id) {
		return commService.getUserComm(user_id);
	}
	
	// 공통사항 저장
	@PostMapping("/save")
	public ResponseEntity<Map<String, Object>> setUserInfo(HttpSession session, HttpServletRequest request, @RequestBody CommVO vo) {
		Map<String, Object> response = new HashMap<>();
		
		int updatedRows = commService.setUserComm(vo);
		
		if (updatedRows > 0) {
			response.put("success", true);
			response.put("message", "공통사항 저장 성공");
			return ResponseEntity.ok(response);
		} else {
			response.put("success", false);
			response.put("message", "공통사항 저장 오류");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
	
}
