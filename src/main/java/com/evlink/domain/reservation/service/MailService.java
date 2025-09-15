package com.evlink.domain.reservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.evlink.domain.mypage.vo.ChargerVO;
import com.evlink.domain.reservation.vo.ReservationVO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	@Autowired
	private JavaMailSender mailSender;

	public void sendEmail(String toEmail, ReservationVO rvo, ChargerVO cvo) {
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom("akdlrhfna@naver.com");
			helper.setTo(toEmail);
			helper.setSubject("EVLink 예약 알림 서비스");
			// 로컬 환경 테스트용 URL
			String linkUrl = "http://localhost:3000";
			String imageUrl = "http://localhost:3000/images/EVLink_logo.png";

			String body = "<html>"	+ "<body>"
					+ "<h1>충전소 예약에 대한 알림 서비스입니다.</h1>"
					+ "<p>충전소 위치 : " + "<strong>" + cvo.getAddr() + " " + cvo.getAddrDetail() + "</strong></p>"
					+ "<p>예약자 : " + "<strong>" + rvo.getResNm() + "</strong></p>" 
					+ "<p>예약 날짜 : " + "<strong>" + rvo.getResDate() + "</strong></p>"
					+ "<p>예약 시간 : " + "<strong>" + rvo.getResStartTime() + "</strong>" + " ~ " + "<strong>" + rvo.getResEndTime() + "</strong></p>"
					+ "<p>결제 내역 : " + "<strong>" + rvo.getPayTotalHour() + "</strong> 원</p>"
					+ "<a href=\"" + linkUrl + "\">" + "<img src=\"" + imageUrl	+ "\" alt=\"결제 내역 메일링크\" style=\"width:150px; height:auto; border:0;\" />" + "</a>"
					+ "</body>"	+ "</html>";
			helper.setText(body, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			System.out.println("이메일 발송이 취소되었습니다");
			throw new RuntimeException(e);
		}
	}

}
