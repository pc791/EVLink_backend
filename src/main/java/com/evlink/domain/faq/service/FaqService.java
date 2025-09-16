package com.evlink.domain.faq.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evlink.domain.faq.dao.FaqDao;

import com.evlink.domain.faq.vo.FaqVO;

@Service
public class FaqService {
    @Autowired
    private FaqDao faqDao;
    
    public List<FaqVO> faqList() {
        return faqDao.list();
    }
}