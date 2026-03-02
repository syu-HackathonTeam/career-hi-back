package com.careerhi.api.domain.auth.service;

import com.careerhi.api.domain.auth.dto.LoginRequest;
import com.careerhi.api.domain.auth.dto.SignupRequest;
import com.careerhi.api.domain.auth.dto.SignupResponse;
import com.careerhi.api.domain.auth.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository; // [추가]

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

    @Transactional
    public void logout(String accessToken) {
        // 1. Access Token에서 유저 정보를 가져옴 (유효성 검증은 SecurityFilter에서 이미 끝난 상태)
        String email = jwtTokenProvider.getEmailFromToken(accessToken);

        // 2. DB에 저장된 해당 유저의 Refresh Token 삭제 (다음에 reissue 요청이 오면 실패하게 됨)
        refreshTokenRepository.deleteByEmail(email);

        // 3. (심화) Redis를 쓰고 있다면 Access Token을 블랙리스트에 등록해서
        // 만료 전까지 재사용 못 하게 막는 로직을 여기에 추가 예정.... 언젠가 .. 하겠지 ...
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