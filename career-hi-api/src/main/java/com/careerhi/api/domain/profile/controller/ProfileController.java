package com.careerhi.api.domain.profile.controller;

import com.careerhi.api.domain.profile.dto.ProfileResponse;
import com.careerhi.api.domain.profile.dto.ProfileSaveRequest;
import com.careerhi.api.domain.profile.service.ProfileService;
import com.careerhi.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ApiResponse<Void> createProfile(
            @AuthenticationPrincipal UserDetails userDetails, // 토큰에서 유저 정보 추출
            @Valid @RequestBody ProfileSaveRequest request
    ) {
        // userDetails.getUsername()에는 우리가 넣었던 'email'이 들어있습니다.
        profileService.saveProfile(userDetails.getUsername(), request);
        return ApiResponse.success("프로필 정보가 저장되었습니다.");
    }

    @GetMapping
    public ApiResponse<ProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        ProfileResponse response = profileService.getProfile(userDetails.getUsername());
        return ApiResponse.success("프로필 조회에 성공하였습니다.", response);
    }
}