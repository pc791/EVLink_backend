package com.evlink.domain.faq.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.evlink.domain.faq.service.FaqService;
import com.evlink.domain.faq.vo.FaqVO;

@RestController
@RequestMapping("/faq")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://192.168.0.90:3000", "http://192.168.0.90:3001"})
public class FaqController {

    private final FaqService faqService;
    
    public FaqController(FaqService faqService) { 
        this.faqService = faqService; 
    }

    // 최종 URL: http://localhost:8080/TeamCProject_final/faq/list
    @GetMapping("/list")
    public List<FaqVO> list() {
        return faqService.faqList();
    }
}