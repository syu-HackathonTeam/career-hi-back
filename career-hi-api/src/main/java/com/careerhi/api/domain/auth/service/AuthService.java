package com.careerhi.api.domain.auth.service;

import com.careerhi.api.domain.auth.dto.*;
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
     * [1] 회원가입 (인증번호 최종 검증 단계 추가)
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. DB에서 해당 번호로 발송된 가장 최근 인증 코드 조회
        VerificationCode verification = verificationCodeRepository.findTopByPhoneNumberAndTypeOrderByExpiryDateDesc(
                        request.getPhoneNumber(), "SIGNUP")
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_INVALID_CODE));

        // 2. 인증 코드 유효성 검증 (만료 여부 및 번호 일치 여부)
        if (verification.isExpired()) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        if (!verification.getAuthCode().equals(request.getAuthCode())) {
            throw new CustomException(ErrorCode.AUTH_INVALID_CODE);
        }

        // 3. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }

        // 4. 유저 저장
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .phoneNumber(request.getPhoneNumber())
                .marketingAgreed(request.getMarketingAgreed())
                .build();

        userRepository.save(newUser);

        // 5. 보안: 가입 완료 후 사용된 인증 코드 삭제 (재사용 방지)
        verificationCodeRepository.deleteByPhoneNumberAndType(request.getPhoneNumber(), "SIGNUP");

        return createTokenResponse(newUser);
    }

    @Transactional(readOnly = true)
    public SignupResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        return createTokenResponse(user);
    }

    @Transactional
    public void logout(String accessToken) {
        String email = jwtTokenProvider.getEmailFromToken(accessToken);
        refreshTokenRepository.deleteByEmail(email);
    }

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
            System.out.println("수신번호: " + request.phoneNumber());
            System.out.println("인증번호: " + authCode);
            System.out.println("==================================");
        } else {
            smsService.sendVerificationCode(request.phoneNumber(), authCode);
        }

        return 180;
    }

    /**
     * [참고] UI에서 '인증 확인' 버튼 클릭 시 호출되는 메서드
     * 가입 단계에서 한 번 더 확인하므로, 여기서는 삭제 로직을 빼는 것이 흐름상 안전할 수 있습니다.
     */
    @Transactional(readOnly = true)
    public void checkVerificationCode(VerificationCheckRequest request) {
        VerificationCode code = verificationCodeRepository.findTopByPhoneNumberAndTypeOrderByExpiryDateDesc(
                        request.phoneNumber(), request.type())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_INVALID_CODE));

        if (code.isExpired()) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        if (!code.getAuthCode().equals(request.authCode())) {
            throw new CustomException(ErrorCode.AUTH_INVALID_CODE);
        }
    }

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
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .accessTokenExpiresIn(3600L)
                        .refreshTokenExpiresIn(1209600L)
                        .build())
                .build();
    }
}