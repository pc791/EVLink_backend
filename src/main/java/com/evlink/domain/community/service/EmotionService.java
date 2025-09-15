package com.evlink.domain.community.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evlink.domain.community.dao.EmotionDao;
import com.evlink.domain.community.vo.EmotionVO;
import com.evlink.global.vo.PageVO;


@Service
public class EmotionService {
	@Autowired
	private EmotionDao emotionDao;
	
	public Map<String, Object> showList(int cPage, String searchType, String searchValue) {
        PageVO page = new PageVO();        // 기본값: nowPage=1, numPerPage=10, pagePerBlock=5
        page.setNowPage(cPage);

        // 1) 전체 레코드 수 가져오기
        int totalRecord = emotionDao.totalcount(searchType, searchValue);
        page.setTotalRecord(totalRecord);

        // 2) 총 페이지 계산
        int numPerPage = page.getNumPerPage();
        int totalPage = (int) Math.ceil((double) totalRecord / numPerPage);
        page.setTotalPage(totalPage);

        // 3) 총 블럭, 현재 블럭 계산
        int pagePerBlock = page.getPagePerBlock();
        int totalBlock = (int) Math.ceil((double) totalPage / pagePerBlock);
        page.setTotalBlock(totalBlock);
        int nowBlock = (int) Math.floor((double)(page.getNowPage()-1) / pagePerBlock) + 1;
        page.setNowBlock(nowBlock);

        // 4) begin / end index 계산 (DB용 startRow)
        int beginPerPage = (page.getNowPage() - 1) * numPerPage; // 0-based offset for MySQL
        int endPerPage = beginPerPage + numPerPage - 1;
        page.setBeginPerPage(beginPerPage);
        page.setEndPerPage(endPerPage);

        // 5) 화면에 보여줄 startPage, endPage 계산 (블럭 단위)
        int startPage = (nowBlock - 1) * pagePerBlock + 1;
        int endPage = Math.min(startPage + pagePerBlock - 1, totalPage);

        // 6) 실제 데이터 조회
        List<EmotionVO> data = Collections.emptyList();
        if (totalRecord > 0) {
            data = emotionDao.showList(beginPerPage, numPerPage, searchType, searchValue);
        }

        // 7) 프론트가 기대하는 구조로 반환
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("currentPage", page.getNowPage());
        result.put("totalItems", totalRecord);
        result.put("totalPages", totalPage);
        result.put("startPage", startPage);
        result.put("endPage", endPage);
        // (원하면 pageVO 전체도 전달 가능)
        result.put("pageVO", page);

        return result;
    }
	public void insertContent(EmotionVO vo) {
		emotionDao.insertContent(vo);
	}
	
	public EmotionVO showDetail(long emo_id) {
		emotionDao.uphit(emo_id);
		return emotionDao.showDetail(emo_id);
	}

	public void update(EmotionVO vo) {
		emotionDao.update(vo);
	}
	public void delete(long emo_id) {
		emotionDao.delete(emo_id);
	}
}
