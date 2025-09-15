package com.evlink;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

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

        // nameAttributeKey를 email로 고정
        return new DefaultOAuth2User(
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            std,
            "email"
        );
    }
}
