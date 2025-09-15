package com.evlink.domain.login.controller;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin 제거(전역 CORS 사용)
public class AuthController {

    /** http(s)://host:port + context-path 까지 자동 포함 */
    private String ctxBase() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .build().toUriString();
    }

    @GetMapping("/google")
    public ResponseEntity<Map<String, String>> google() {
        return ResponseEntity.ok(Map.of("url", ctxBase() + "/oauth2/authorization/google"));
    }

    @GetMapping("/kakao")
    public ResponseEntity<Map<String, String>> kakao() {
    	System.out.println("전송");
        return ResponseEntity.ok(Map.of("url", ctxBase() + "/oauth2/authorization/kakao"));
    }

    @GetMapping("/naver")
    public ResponseEntity<Map<String, String>> naver() {
        return ResponseEntity.ok(Map.of("url", ctxBase() + "/oauth2/authorization/naver"));
    }

    /** 세션 기반 로그인 상태 조회 (프론트 키와 100% 일치하게 수동 구성) */
    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> session(
            @AuthenticationPrincipal OAuth2User user,
            Authentication authentication
    ) {
        Map<String, Object> body = new HashMap<>();

        if (authentication == null || user == null) {
            body.put("isLoggedIn", false);
            return ResponseEntity.ok(body);
        }

        // provider(registrationId)
        String provider = (authentication instanceof OAuth2AuthenticationToken ot)
                ? ot.getAuthorizedClientRegistrationId()
                : null;

        // CustomOAuth2UserService에서 표준화된 속성: { email, provider }
        String email = (String) user.getAttributes().get("email");

        body.put("isLoggedIn", true);   // ★ 프론트가 기대하는 정확한 키
        body.put("email", email);
        body.put("provider", provider);
        body.put("name", null);         // 필요 시 나중에 채우기 (지금은 null)
        body.put("profile", null);      // 필요 시 나중에 채우기 (지금은 null)

        return ResponseEntity.ok(body);
    }

    /** 로그아웃 */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();

        Map<String, Object> body = new HashMap<>();
        body.put("isLoggedIn", false);  // ★ 프론트가 바로 반영
        return ResponseEntity.ok(body);
    }
}
