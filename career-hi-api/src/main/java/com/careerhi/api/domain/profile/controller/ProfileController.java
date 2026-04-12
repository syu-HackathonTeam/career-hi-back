package com.careerhi.api.domain.profile.controller;

import com.careerhi.api.domain.profile.dto.ProfileResponse;
import com.careerhi.api.domain.profile.dto.ProfileSaveRequest;
import com.careerhi.api.domain.profile.service.ProfileService;
import com.careerhi.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3. Profile", description = "사용자 커리어 프로필 관리 API")
@RestController
@RequestMapping("/api/v1/users/me/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "프로필 생성", description = "현재 접속한 사용자의 커리어 정보를 최초 저장합니다.")
    @PostMapping
    public ApiResponse<Void> createProfile(
            @AuthenticationPrincipal UserDetails userDetails, // 토큰에서 유저 정보 추출
            @Valid @RequestBody ProfileSaveRequest request
    ) {
        // userDetails.getUsername()에는 우리가 넣었던 'email'이 들어있습니다.
        profileService.saveProfile(userDetails.getUsername(), request);
        return ApiResponse.success("프로필 정보가 저장되었습니다.");
    }

    @Operation(summary = "내 프로필 조회", description = "현재 로그인된 유저의 프로필 상세 정보를 가져옵니다.")
    @GetMapping
    public ApiResponse<ProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        ProfileResponse response = profileService.getProfile(userDetails.getUsername());
        return ApiResponse.success("프로필 조회에 성공하였습니다.", response);
    }

    @Operation(summary = "프로필 수정", description = "기존 프로필 정보의 일부 또는 전체를 업데이트합니다.")
    @PatchMapping
    public ApiResponse<Void> patchProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileSaveRequest request // PATCH이므로 @Valid는 선택적(부분 데이터만 올 수 있음)
    ) {
        profileService.updateProfile(userDetails.getUsername(), request);
        return ApiResponse.success("프로필 정보가 수정되었습니다.");
    }
}