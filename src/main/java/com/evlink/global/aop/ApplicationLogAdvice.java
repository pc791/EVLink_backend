package com.evlink.global.aop;

import java.util.Collection;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.evlink.global.dao.ApplicationLogDao;
import com.evlink.global.vo.ApplicationLoggerVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
@Aspect
public class ApplicationLogAdvice {
	@Autowired
	private ApplicationLogDao logDao;
	
	// Login Log 기록
	@Around("execution(* com.evlink.domain.login.controller.*.*(..))")
	public Object loginLogger(ProceedingJoinPoint jp) {
		Object[] fd = jp.getArgs();
		Object rpath = null;
		try {
			rpath = jp.proceed();
			createLoggin(fd, jp);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return rpath;
	}
		
	private void createLoggin(Object[] fd, ProceedingJoinPoint jp) {
		ApplicationLoggerVO alvo = new ApplicationLoggerVO();
		if(fd[1] instanceof OAuth2AuthenticationToken) {
			// System.out.println("AOP >> "+fd[1]);

			// 공급자정보
			String registrationId = ((OAuth2AuthenticationToken) fd[1]).getAuthorizedClientRegistrationId();
			// 사용자정보
			OAuth2User oAuth2User = ((OAuth2AuthenticationToken) fd[1]).getPrincipal();
			// 권한
			Collection<? extends GrantedAuthority> authorities = ((OAuth2AuthenticationToken) fd[1]).getAuthorities();

			// System.out.println("AOP >> registrationId:"+registrationId+", oAuth2User"+oAuth2User+", authorities"+authorities);
			// 속성
			Map<String, Object> attrs = oAuth2User.getAttributes();
			// System.out.println("attr >> "+attrs.toString());
			
			Object details = ((OAuth2AuthenticationToken) fd[1]).getDetails();
			// System.out.println("details >> "+details.toString());

            // ip와 session id
			if (details instanceof WebAuthenticationDetails webDetails) {
				alvo.setSession_id(webDetails.getSessionId());
				alvo.setIp_addr(webDetails.getRemoteAddress());
			}
			alvo.setProvider(registrationId); // 공급자명
			alvo.setLogin_id((String) attrs.get("email")); // 로그인ID
			alvo.setUser_id(String.valueOf(attrs.get("userId"))); // 사용자ID
			alvo.setUser_tp((String) attrs.get("userTp")); // 사용자타입
			alvo.setAuth(authorities.toString()); // 권한
			
			logDao.addLogging(alvo);

		}
	}
	
}