package kr.co.ictedu.teamcfinal.VO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthVO {
    private boolean isLoggedIn;
    private String email;
    private String name;
    private String provider;
    private String profile;
}
