package com.evlink.domain.event.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evlink.domain.event.dao.EventDao;

import com.evlink.domain.event.vo.EventVO; 

@Service	
public class EventService {
	@Autowired
	private EventDao eventDao;
 
	public List<EventVO> eventList(Map<String, String> map){
		return eventDao.list(map);
	}

}
