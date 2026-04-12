package com.careerhi.api.domain.auth.controller;

import com.careerhi.api.domain.auth.dto.*;
import com.careerhi.api.domain.auth.service.AuthService;
import com.careerhi.common.exception.CustomException;
import com.careerhi.common.exception.ErrorCode;
import com.careerhi.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Auth", description = "인증 및 계정 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @Operation(summary = "회원가입", description = "신규 사용자를 등록하고 초기 토큰을 발급합니다.")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ApiResponse.success("회원가입이 완료되었습니다. 환영합니다!", response);
    }

    // 로그인 (추가됨)
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 확인하여 Access/Refresh 토큰을 반환합니다.")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SignupResponse> login(@Valid @RequestBody LoginRequest request) {
        SignupResponse response = authService.login(request);
        return ApiResponse.success("로그인에 성공하였습니다.", response);
    }

    @Operation(summary = "로그아웃", description = "서버 측의 리프레시 토큰을 삭제하여 세션을 종료합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE); // 에러 처리
        }

        String accessToken = authHeader.substring(7);
        authService.logout(accessToken);
        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다."));
    }

    // 인증번호 발송
    @Operation(summary = "인증번호 발송", description = "회원가입 또는 인증을 위한 6자리 코드를 SMS로 발급합니다.")
    @PostMapping("/verify/send")
    public ApiResponse<VerificationSendResponse> sendVerificationCode(@Valid @RequestBody VerificationSendRequest request) {
        int expiryTime = authService.sendVerificationCode(request);
        return ApiResponse.success("인증번호가 발송되었습니다.", new VerificationSendResponse(expiryTime));
    }

    @Operation(summary = "인증번호 확인", description = "입력한 인증번호가 유효한지 검증합니다.")
    @PostMapping("/verify/check")
    public ApiResponse<Void> checkVerificationCode(@Valid @RequestBody VerificationCheckRequest request) {
        authService.checkVerificationCode(request);
        return ApiResponse.success("인증이 완료되었습니다.");
    }
}