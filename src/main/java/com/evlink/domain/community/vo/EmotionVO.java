package com.evlink.domain.community.vo;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Alias("emvo")
public class EmotionVO {

	private long emo_id;
	private long user_id;
	private String title;
	private String content;
	private int hit;
	private String del_yn;
    private LocalDateTime reg_dt;
    private LocalDateTime upd_dt;
    private String emotion;
}
