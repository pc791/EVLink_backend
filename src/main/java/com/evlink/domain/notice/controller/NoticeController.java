package com.evlink.domain.notice.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.evlink.domain.notice.service.NoticeService;
import com.evlink.domain.notice.vo.NoticeVO;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService service;

    /** 공지 목록 */
    @GetMapping("/noticeList")
    public List<NoticeVO> noticeList() {
        return service.noticeList();
    }

    /** 상세 조회: 읽기 전용 (조회수 증가 없음) */
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") int id) {
        NoticeVO vo = service.detail(id);
        if (vo == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(vo);
    }

    /** 조회수 증가: 사용자 클릭 시 1회 호출 */
    @PostMapping("/hit/{id}")
    public ResponseEntity<Void> hit(@PathVariable("id") int id) {
        service.hit(id);
        return ResponseEntity.ok().build();
    }
}
