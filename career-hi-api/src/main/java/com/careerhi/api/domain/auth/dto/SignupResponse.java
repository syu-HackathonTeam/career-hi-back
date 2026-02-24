package com.careerhi.api.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {
    private UserInfo user;
    private TokenInfo tokenInfo;

    @Getter
    @Builder
    public static class UserInfo {
        private Long userId;
        private String userName;
        private String email;
    }

    @Getter
    @Builder
    public static class TokenInfo {
        private String grantType;
        private String accessToken;
        private String refreshToken;
        private Long accessTokenExpiresIn;
        private Long refreshTokenExpiresIn;
    }
}