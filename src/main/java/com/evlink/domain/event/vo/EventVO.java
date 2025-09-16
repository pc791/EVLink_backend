package com.evlink.domain.event.vo;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Alias("eventvo")
@Getter
@Setter
public class EventVO {
    private int eventId;        // event_id
    private String title;      // title
    private String content;    //content
    private String startDate;  //start_dt
    private String endDate;    //end_dt 
    private String useyn;     // use_yn 
    private String imageUrl;     // 이미지 경로(없으면 null)

}
