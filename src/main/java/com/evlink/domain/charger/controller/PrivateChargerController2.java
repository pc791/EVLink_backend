package com.evlink.domain.charger.controller;

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



@RestController
@RequestMapping("/charger")
public class PrivateChargerController2 {
//
// @Autowired
// private ChargerService chargerService;
//
// // 개인 충전소 등록
// @PostMapping("/add")
// public ResponseEntity<String> addCharger(@RequestBody ChargerVO vo) {
//     // 인증된 사용자 정보를 SecurityContextHolder에서 가져옴
//     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//     String loginId = authentication.getName(); // 로그인된 사용자의 ID(예: 이메일 또는 사용자 이름)
//
//     // loginId를 사용하여 DB에서 userId 조회 (실제 사용자 ID)
//     Long userId = privateChargerService.getUserIdByLoginId(loginId);
//     if (userId == null) {
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자를 찾을 수 없습니다.");
//     }
//
//     vo.setUserId(userId); // VO에 실제 사용자 ID 설정
//     privateChargerService.addPrivateCharger(vo);
//
//     // log.info("User {} (user_id={}) 충전소 등록 완료", loginId, userId);
//     return ResponseEntity.ok("개인 충전소 등록 성공!");
// }
//
// // 개인 충전소 정보 수정 (userId 없이 chargerId만 사용)
// @PutMapping("/{chargerId}")
// public ResponseEntity<String> updateCharger(@PathVariable("chargerId") long chargerId, @RequestBody PrivateChargerVO vo) {
//     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//     String loginId = authentication.getName();
//     Long userId = privateChargerService.getUserIdByLoginId(loginId);
//     if (userId == null) {
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자를 찾을 수 없습니다.");
//     }
//     
//     vo.setUserId(userId); // 현재 로그인된 사용자 ID를 VO에 설정
//     vo.setChargerId(chargerId);
//
//     // 해당 충전기가 현재 사용자의 소유인지 확인하는 로직 추가
//     boolean isOwner = privateChargerService.isChargerOwnedByUser(chargerId, userId);
//     if (!isOwner) {
//         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
//     }
//
//     int updatedRows = privateChargerService.updateCharger(vo);
//     
//     if (updatedRows > 0) {
//         return ResponseEntity.ok("개인 충전소 정보 수정 성공!");
//     } else {
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("수정하려는 충전소 정보가 존재하지 않습니다.");
//     }
// }
// 
// // 개인 충전소 정보 삭제 (userId 없이 chargerId만 사용)
// @DeleteMapping("/{chargerId}")
// public ResponseEntity<String> deleteCharger(@PathVariable("chargerId") long chargerId) {
//     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//     String loginId = authentication.getName();
//     Long userId = privateChargerService.getUserIdByLoginId(loginId);
//     if (userId == null) {
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자를 찾을 수 없습니다.");
//     }
//
//     // 해당 충전기가 현재 사용자의 소유인지 확인하는 로직 추가
//     boolean isOwner = privateChargerService.isChargerOwnedByUser(chargerId, userId);
//     if (!isOwner) {
//         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
//     }
//     
//     int deletedRows = privateChargerService.deleteCharger(chargerId);
//
//     if (deletedRows > 0) {
//         return ResponseEntity.ok("개인 충전소 정보 삭제 성공!");
//     } else {
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제하려는 충전소 정보가 존재하지 않습니다.");
//     }
// }
}