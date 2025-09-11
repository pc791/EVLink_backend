package com.evlink.domain.mypage.vo;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("mpuser")
public class CommVO {
		private int user_id;
		private String user_tp;
		private String login_id;
		private String user_nicknm;
		private String user_nm;
		private String user_phone;
}
