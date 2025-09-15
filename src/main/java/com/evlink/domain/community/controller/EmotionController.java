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

import com.evlink.domain.community.service.EmotionService;
import com.evlink.domain.community.vo.EmotionVO;

@RestController
@RequestMapping("/emotion")
public class EmotionController {

	@Autowired
	private EmotionService emotionService;
	
	@GetMapping("/list")
    public ResponseEntity<Map<String, Object>> showList(
            @RequestParam(name = "cPage", defaultValue = "1") int cPage,
            @RequestParam(name = "searchType", defaultValue = "") String searchType,
            @RequestParam(name = "searchValue", defaultValue = "") String searchValue) {

        Map<String, Object> pageData = emotionService.showList(cPage, searchType, searchValue);
        return ResponseEntity.ok(pageData);
    }
	@RequestMapping("/insert")
	public void insertContent(@ModelAttribute EmotionVO vo) {
		try {
			emotionService.insertContent(vo);
			System.out.println("등록 완료.");
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	@GetMapping("/detail")
	public EmotionVO showDetail(@RequestParam("emo_id") long emo_id) {
		return emotionService.showDetail(emo_id);
	}
	@PostMapping("/update")
	public void update(@RequestBody EmotionVO vo) {
		emotionService.update(vo);
	}
	@GetMapping("/delete")
	public void delete(@RequestParam("emo_id") long emo_id) {
		emotionService.delete(emo_id);
	}
}
