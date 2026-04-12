package com.careerhi.api.domain.auth.service;

import com.careerhi.api.domain.auth.dto.*;
import com.careerhi.api.domain.auth.entity.RefreshToken;
import com.careerhi.api.domain.auth.entity.VerificationCode;
import com.careerhi.api.domain.auth.repository.RefreshTokenRepository;
import com.careerhi.api.domain.auth.repository.VerificationCodeRepository;
import com.careerhi.api.domain.user.entity.User;
import com.careerhi.api.domain.user.repository.UserRepository;
import com.careerhi.api.global.infra.sms.SmsService;
import com.careerhi.api.global.jwt.JwtTokenProvider;
import com.careerhi.common.exception.ErrorCode;
import com.careerhi.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final SmsService smsService;

    @Value("${coolsms.test-mode:true}")
    private boolean isTestMode;

    /**
     * [1] 회원가입
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. 인증 코드 검증
        VerificationCode verification = verificationCodeRepository.findTopByPhoneNumberAndTypeOrderByExpiryDateDesc(
                        request.getPhoneNumber(), "SIGNUP")
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_INVALID_CODE));

        if (verification.isExpired()) throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        if (!verification.getAuthCode().equals(request.getAuthCode())) throw new CustomException(ErrorCode.AUTH_INVALID_CODE);

        // 2. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) throw new CustomException(ErrorCode.EMAIL_DUPLICATED);

        // 3. 유저 저장
        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .marketingAgreed(request.getMarketingAgreed())
                .build();
        userRepository.save(newUser);

        // 4. 사용된 인증 코드 삭제
        verificationCodeRepository.deleteByPhoneNumberAndType(request.getPhoneNumber(), "SIGNUP");

        return createTokenResponse(newUser);
    }

    /**
     * [2] 로그인 (토큰 저장을 위해 readOnly=true 제거)
     */
    @Transactional
    public SignupResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        return createTokenResponse(user);
    }

    /**
     * [3] 로그아웃
     */
    @Transactional
    public void logout(String accessToken) {
        String email = jwtTokenProvider.getEmailFromToken(accessToken);
        // DB에서 해당 유저의 리프레시 토큰을 삭제하여 재발급을 원천 차단
        refreshTokenRepository.deleteByEmail(email);
    }

    /**
     * [4] 공통 토큰 생성 및 리프레시 토큰 DB 저장 로직
     */
    private SignupResponse createTokenResponse(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshTokenString = jwtTokenProvider.createRefreshToken();

        // 1. 기존에 이 이메일로 저장된 리프레시 토큰이 있는지 확인
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByEmail(user.getEmail())
                .map(existingToken -> {
                    // 이미 있다면 토큰 값만 갱신
                    existingToken.updateToken(refreshTokenString);
                    return existingToken;
                })
                .orElseGet(() -> {
                    // 없다면 새로 생성
                    return RefreshToken.builder()
                            .email(user.getEmail())
                            .token(refreshTokenString)
                            .build();
                });

        // 2. DB 저장 (갱신 혹은 신규 저장)
        refreshTokenRepository.save(refreshTokenEntity);

        return SignupResponse.builder()
                .user(SignupResponse.UserInfo.builder()
                        .userId(user.getId())
                        .userName(user.getName())
                        .email(user.getEmail())
                        .build())
                .tokenInfo(SignupResponse.TokenInfo.builder()
                        .grantType("Bearer")
                        .accessToken(accessToken)
                        .refreshToken(refreshTokenString)
                        .accessTokenExpiresIn(3600L)
                        .refreshTokenExpiresIn(1209600L)
                        .build())
                .build();
    }

    /**
     * [5] 인증번호 발송 및 체크 (기존 로직 유지)
     */
    @Transactional
    public int sendVerificationCode(VerificationSendRequest request) {
        verificationCodeRepository.deleteByPhoneNumberAndType(request.phoneNumber(), request.type());
        String authCode = String.format("%06d", new Random().nextInt(1000000));
        VerificationCode verification = VerificationCode.builder()
                .phoneNumber(request.phoneNumber())
                .authCode(authCode)
                .type(request.type())
                .expiryDate(LocalDateTime.now().plusMinutes(3))
                .build();
        verificationCodeRepository.save(verification);

        if (isTestMode) {
            System.out.println("======= [CONSOL TEST MODE] =======");
            System.out.println("인증번호: " + authCode);
            System.out.println("==================================");
        } else {
            smsService.sendVerificationCode(request.phoneNumber(), authCode);
        }
        return 180;
    }

    @Transactional(readOnly = true)
    public void checkVerificationCode(VerificationCheckRequest request) {
        VerificationCode code = verificationCodeRepository.findTopByPhoneNumberAndTypeOrderByExpiryDateDesc(
                        request.phoneNumber(), request.type())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_INVALID_CODE));
        if (code.isExpired()) throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        if (!code.getAuthCode().equals(request.authCode())) throw new CustomException(ErrorCode.AUTH_INVALID_CODE);
    }
}