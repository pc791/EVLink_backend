package com.evlink.domain.notice.vo;

import org.apache.ibatis.type.Alias;
import lombok.Getter;
import lombok.Setter;

@Alias("noticevo")
@Getter
@Setter
public class NoticeVO {
    private int noticeId;        // notice_id
    private String title;      // title
    private String content;    //content
    private String noticeDt;   //notice_dt
    private int hit;             //hit
    private String majoryn; // major_yn
    private String useyn;     // use_yn 
}