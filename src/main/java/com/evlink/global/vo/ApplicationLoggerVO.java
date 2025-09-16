package com.evlink.global.vo;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("alvo")
public class ApplicationLoggerVO {
	private int log_id;          // log_id
	private String session_id;   // session_id
	private String ip_addr;      // 사용자 ip
	private String provider;     // 공급자명
	private String login_id;     // 로그인ID
	private String user_id;      // 사용자ID
	private String user_tp;      // 사용자타입
	private String auth;         // 권한
	private String req_dt;       // 요청일시
}
