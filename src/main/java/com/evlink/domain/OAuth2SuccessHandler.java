package kr.co.ictedu.teamcfinal.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private static final String FRONTEND_URL = "http://localhost:3000";

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) {
    try {
      // 세션(JSESSIONID) 만들어진 상태 → 프론트로 이동
      response.sendRedirect(FRONTEND_URL + "/");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
