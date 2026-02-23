package com.careerhi.api.global.exception;

import com.careerhi.common.exception.CustomException;
import com.careerhi.common.exception.ErrorCode;
import com.careerhi.common.response.ApiResponse; // ★ ApiResponse 위치에 맞춰 Import
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // [1] 우리가 직접 발생시킨 CustomException 처리 (예: 중복 이메일, 비번 불일치)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.warn("CustomException Occurred: {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus()) // ErrorCode에 정의된 HTTP 상태코드 사용
                .body(ApiResponse.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
    }

    // [2] @Valid 유효성 검사 실패 처리 (예: 이메일 형식이 아님, 빈 값)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<Object>>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation Failed: {}", e.getBindingResult().getFieldError().getDefaultMessage());

        // 첫 번째 에러 메시지만 대표로 보여주거나, 리스트로 가공
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();

        return ResponseEntity
                .status(400)
                .body(ApiResponse.error("INVALID_INPUT_VALUE", errorMessage));
    }

    // [3] 그 외 알 수 없는 서버 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }
}