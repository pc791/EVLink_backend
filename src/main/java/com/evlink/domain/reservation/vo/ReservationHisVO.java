package com.evlink.domain.reservation.vo;

import java.time.LocalTime;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@Alias("reshis")
public class ReservationHisVO {
	private int res_id;
	private int charger_id;
	private int user_id;
	private String res_nm;
	private String res_tel;
	private String res_email;
	private String res_date;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime res_start_time;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime res_end_time;
	private String pay_total_hour;
	private String res_status;
	private String use_status;
	private String reg_dt;
	private String car_model;
	private String chager_addr;
}
