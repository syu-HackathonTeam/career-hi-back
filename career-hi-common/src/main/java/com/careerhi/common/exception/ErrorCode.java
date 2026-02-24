package com.careerhi.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Auth (인증)
    AUTH_INVALID_CODE(400, "AUTH_INVALID_CODE", "인증번호가 일치하지 않습니다."),
    INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "입력값이 올바르지 않습니다."),
    LOGIN_FAILED(401, "LOGIN_FAILED", "아이디 또는 비밀번호를 확인해주세요."),
    TOKEN_EXPIRED(401, "TOKEN_EXPIRED", "세션이 만료되었습니다. 다시 로그인해주세요."),
    ACCESS_DENIED(403, "ACCESS_DENIED", "접근 권한이 없습니다."),

    // User (회원)
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "가입되지 않은 회원입니다."),
    EMAIL_DUPLICATED(409, "EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다."),

    // Profile (프로필) ★ 추가됨
    PROFILE_NOT_FOUND(404, "PROFILE_NOT_FOUND", "프로필 정보를 찾을 수 없습니다."),

    // Report (리포트) ★ 추가됨
    REPORT_NOT_FOUND(404, "REPORT_NOT_FOUND", "생성된 리포트를 찾을 수 없습니다."),
    AI_SERVICE_ERROR(500, "AI_SERVICE_ERROR", "AI 분석 중 오류가 발생했습니다."),

    // Server (서버 공통)
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}