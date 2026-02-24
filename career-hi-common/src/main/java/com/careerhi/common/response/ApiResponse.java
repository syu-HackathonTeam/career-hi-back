package com.careerhi.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", null, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>("SUCCESS", message, null);
    }

    // 에러 응답 생성
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>("ERROR", message, null);
        // 실제로는 ErrorResponse 객체를 data에 넣거나 별도 필드를 쓸 수도 있지만,
        // 일단 명세서 포맷인 status, message 위주로 구현합니다.
    }
}