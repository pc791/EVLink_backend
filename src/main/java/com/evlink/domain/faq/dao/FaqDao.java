package com.evlink.domain.faq.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.evlink.domain.faq.vo.FaqVO;

@Mapper
public interface FaqDao {
    List<FaqVO> list();
}