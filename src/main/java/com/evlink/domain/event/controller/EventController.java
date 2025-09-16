package com.evlink.domain.event.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evlink.domain.event.service.EventService;

import com.evlink.domain.event.vo.EventVO; 

@RestController
@RequestMapping("/event")
@CrossOrigin(origins= {"http://localhost:3000", "http://localhost:3001"})
public class EventController {
	
    @Autowired
	private EventService eventService;
	
	
    @GetMapping("/list")
    public ResponseEntity<List<EventVO>> eventlist(@RequestParam Map<String, String> params) {
        List<EventVO> result = eventService.eventList(params);
        return ResponseEntity.ok(result);
    }
}