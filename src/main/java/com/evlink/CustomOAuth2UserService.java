package com.evlink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.evlink.domain.login.dao.AuthDao;
import com.evlink.domain.login.vo.UserVO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	@Autowired
	private AuthDao authDao;
	
    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2User o = super.loadUser(req);
        String provider = req.getClientRegistration().getRegistrationId();
        Map<String, Object> attr = o.getAttributes();

        String email = null;

        if ("google".equals(provider)) {
            // 권장 scope: openid, email
            email = (String) attr.get("email");
        } else if ("kakao".equals(provider)) {
            Map<String,Object> acc = (Map<String,Object>) attr.get("kakao_account");
            if (acc != null) email = (String) acc.get("email");
        } else if ("naver".equals(provider)) {
            Map<String,Object> res = (Map<String,Object>) attr.get("response");
            if (res != null) email = (String) res.get("email");
        }

        // email 필수 요구. 없으면 예외(동의해제/비공개 케이스)
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Email consent is required for " + provider + " login.");
        }

        // 표준화된 속성 딱 2개만 (email, provider)
        Map<String, Object> std = new HashMap<>();
        std.put("email", email);
        std.put("provider", provider);
        
        // 2025.09.16 회원가입 및 로그인 처리
        Map<String, String> usrMap = new HashMap<>();
        String joinType = "";
        switch(provider) {
        	case "naver": 
        		joinType = "002";
        		break;
        	case "kakao":
        		joinType = "003";
    			break;
        	case "google":
        		joinType = "003";
				break;
        	case "facebook":
        		joinType = "004";
				break;
			default:
				joinType = "001";
        }
        	
        usrMap.put("login_id", email);
        usrMap.put("join_type", joinType);
        
        int userCnt = authDao.checkSignUp(usrMap);
        // System.out.println("userCnt >> "+userCnt);
        // 회원이 아닌 경우 회원가입
        if(userCnt == 0) {
        	authDao.signUp(usrMap);
        }
        
    	UserVO usrvo = authDao.getUserInfo(usrMap);
    	// System.out.println("usrvo >> "+usrvo.toString());
    	// login session에 담을 data 설정
    	std.put("userId", usrvo.getUser_id());
    	std.put("userTp", usrvo.getUser_tp());
        
        // end

        // nameAttributeKey를 email로 고정
        return new DefaultOAuth2User(
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            std,
            "email"
        );
    }
}
