package com.evlink.domain.event.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;  
import com.evlink.domain.event.vo.EventVO;  // ← 새 경로로 변경

@Mapper
public interface EventDao {
	
	List<EventVO> list(Map<String, String> map);
	
}
