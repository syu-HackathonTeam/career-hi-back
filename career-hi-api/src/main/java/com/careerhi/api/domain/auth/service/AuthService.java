package com.careerhi.api.domain.auth.service;

import com.careerhi.api.domain.auth.dto.LoginRequest;
import com.careerhi.api.domain.auth.dto.SignupRequest;
import com.careerhi.api.domain.auth.dto.SignupResponse;
import com.careerhi.api.domain.user.entity.User;
import com.careerhi.api.domain.user.repository.UserRepository;
import com.careerhi.api.global.jwt.JwtTokenProvider;
import com.careerhi.common.exception.ErrorCode;
import com.careerhi.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider; // 토큰 생성기 주입

    // [1] 회원가입
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            // ★ 수정됨
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .phoneNumber(request.getPhoneNumber())
                .marketingAgreed(request.getMarketingAgreed())
                .build();

        userRepository.save(newUser);

        // ★ 진짜 토큰 발급으로 교체
        return createTokenResponse(newUser);
    }

    // [2] 로그인 (새로 추가됨)
    @Transactional(readOnly = true)
    public SignupResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                // ★ 수정됨
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // ★ 수정됨
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // 3. 토큰 발급 및 응답
        return createTokenResponse(user);
    }

    // [공통] 토큰 응답 생성 메서드 (회원가입/로그인에서 같이 씀)
    private SignupResponse createTokenResponse(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        return SignupResponse.builder()
                .user(SignupResponse.UserInfo.builder()
                        .userId(user.getId())
                        .userName(user.getName())
                        .email(user.getEmail())
                        .build())
                .tokenInfo(SignupResponse.TokenInfo.builder()
                        .grantType("Bearer")
                        .accessToken(accessToken) // 진짜 토큰!
                        .refreshToken(refreshToken)
                        .accessTokenExpiresIn(3600L)
                        .refreshTokenExpiresIn(1209600L)
                        .build())
                .build();
    }
}