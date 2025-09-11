package com.evlink.domain.mypage.vo;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("mpcar")
public class CarVO {
	private int car_id;
	private int user_id;
	private String car_plate;
	private String car_mf;
	private String car_model;
	private String car_year;
	private String car_socket;
	private String reg_dt;
	private String upd_dt;
}
