package com.evlink.domain.notice.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evlink.domain.notice.dao.NoticeDao;

import lombok.RequiredArgsConstructor;
import com.evlink.domain.notice.vo.NoticeVO;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeDao dao;

    @Transactional(readOnly = true)
    public List<NoticeVO> noticeList() {
        return dao.noticeList();
    }

    /** 상세 조회: 읽기 전용 (조회수 증가 없음) */
    @Transactional(readOnly = true)
    public NoticeVO detail(int noticeId) {
        return dao.detail(noticeId);
    }

    /** 조회수 +1: 클릭 시 1회 호출 */
    @Transactional
    public void hit(int noticeId) {
        dao.updateHit(noticeId);
    }
}
