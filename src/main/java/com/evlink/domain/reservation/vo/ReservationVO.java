package com.evlink.domain.reservation.vo;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;


@Alias("reservation")
@Data
public class ReservationVO {
	private Long resId;
	private Long chargerId;
	private Long userId;
	private String resNm;
	private String resTel;
	private String resEmail;
	private Date resDate;
	private LocalTime resStartTime;
	private LocalTime resEndTime;	
	private String payTotalHour;
	private String resStatus;
	private String useStatus;
	private LocalDateTime regDt;
	private LocalDateTime updDt;
}
