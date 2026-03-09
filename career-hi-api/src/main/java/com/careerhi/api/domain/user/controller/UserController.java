package com.careerhi.api.domain.user.controller;

import com.careerhi.api.domain.user.dto.UserResponse;
import com.careerhi.api.domain.user.service.UserService;
import com.careerhi.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // userDetails.getUsername()은 보통 이메일이 담겨 있습니다.
        UserResponse response = userService.getCurrentUserInfo(userDetails.getUsername());
        return ApiResponse.success("사용자 정보 조회 성공", response);
    }
}