package com.evlink.domain.notice.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.evlink.domain.notice.vo.NoticeVO;

@Mapper
public interface NoticeDao {

    // 전체 공지사항 (필독 먼저, 최신순 정렬은 SQL에서 처리)
    List<NoticeVO> noticeList();

    // 필독 공지만 따로 보고 싶을 경우 (선택적)
    List<NoticeVO> majorNoticeList();

    // 상세 조회
    NoticeVO detail(@Param("noticeId") int noticeId);

    // 조회수 증가
    int updateHit(@Param("noticeId") int noticeId);
}
