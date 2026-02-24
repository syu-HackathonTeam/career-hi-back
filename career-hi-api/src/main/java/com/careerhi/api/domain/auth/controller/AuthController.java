package com.careerhi.api.domain.auth.controller;

import com.careerhi.api.domain.auth.dto.LoginRequest;
import com.careerhi.api.domain.auth.dto.SignupRequest;
import com.careerhi.api.domain.auth.dto.SignupResponse;
import com.careerhi.api.domain.auth.service.AuthService;
import com.careerhi.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ApiResponse.success("회원가입이 완료되었습니다. 환영합니다!", response);
    }

    // 로그인 (추가됨)
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SignupResponse> login(@Valid @RequestBody LoginRequest request) {
        SignupResponse response = authService.login(request);
        return ApiResponse.success("로그인에 성공하였습니다.", response);
    }
}