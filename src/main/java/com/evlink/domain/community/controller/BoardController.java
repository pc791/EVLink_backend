package com.evlink.domain.community.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evlink.domain.community.service.BoardService;
import com.evlink.domain.community.vo.BoardVO;


@RestController
@RequestMapping("/board")
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	@GetMapping("/list")
    public ResponseEntity<Map<String, Object>> showList(
            @RequestParam(name = "cPage", defaultValue = "1") int cPage,
            @RequestParam(name = "searchType", defaultValue = "") String searchType,
            @RequestParam(name = "searchValue", defaultValue = "") String searchValue) {

        Map<String, Object> pageData = boardService.showList(cPage, searchType, searchValue);
        return ResponseEntity.ok(pageData);
    }
	@RequestMapping("/insert")
	public void insertContent(@ModelAttribute BoardVO vo) {
		try {
			boardService.insertContent(vo);
			System.out.println(vo.getScore());
			System.out.println("등록 완료.");
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	@GetMapping("/detail")
	public BoardVO showDetail(@RequestParam("board_id") long board_id) {
		return boardService.showDetail(board_id);
	}
	@PostMapping("/update")
	public void update(@RequestBody BoardVO vo) {
		boardService.update(vo);
	}
	@GetMapping("/delete")
	public void delete(long board_id) {
		boardService.delete(board_id);
	}
}
