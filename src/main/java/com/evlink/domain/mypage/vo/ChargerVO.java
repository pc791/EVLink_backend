package com.evlink.domain.mypage.vo;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Alias("charger")
@Data
public class ChargerVO {	
	private Long chargerId;
//	@JsonProperty("user_id")
	private Long userId;
	private String addr;
	private String addrDetail;
	private String chargerTel;
	private double latitude;
	private double longitude;
	private String chargerTp;
	private String chargerSocket;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime openTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime closeTime;
	private String resYn;
	private String payHour;
	private String payTotal;
	private String remarks;
	private String operator;
	private String membership;
	private String supplyPower;
	private LocalDateTime regDt;
	private LocalDateTime updDt;
	private String safeChargerTel;
}
