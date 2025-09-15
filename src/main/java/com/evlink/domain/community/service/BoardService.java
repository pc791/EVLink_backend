package com.evlink.domain.community.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evlink.domain.community.dao.BoardDao;
import com.evlink.domain.community.vo.BoardVO;
import com.evlink.global.vo.PageVO;


@Service
public class BoardService {
	@Autowired
	private BoardDao boardDao;
	
	public Map<String, Object> showList(int cPage, String searchType, String searchValue) {
        PageVO page = new PageVO();        // 기본값: nowPage=1, numPerPage=10, pagePerBlock=5
        page.setNowPage(cPage);

        // 1) 전체 레코드 수 가져오기
        int totalRecord = boardDao.totalcount(searchType, searchValue);
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
        List<BoardVO> data = Collections.emptyList();
        if (totalRecord > 0) {
            data = boardDao.showList(beginPerPage, numPerPage, searchType, searchValue);
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
	public void insertContent(BoardVO vo) {
		boardDao.insertContent(vo);
	}
	
	public BoardVO showDetail(long board_id) {
		boardDao.uphit(board_id);
		return boardDao.showDetail(board_id);
	}

	public void update(BoardVO vo) {
		boardDao.update(vo);
	}
	public void delete(long board_id) {
		boardDao.delete(board_id);
	}
}
