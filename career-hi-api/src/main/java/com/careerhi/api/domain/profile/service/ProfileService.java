package com.careerhi.api.domain.profile.service;

import com.careerhi.api.domain.profile.dto.ProfileResponse;
import com.careerhi.api.domain.profile.dto.ProfileSaveRequest;
import com.careerhi.api.domain.profile.entity.Profile;
import com.careerhi.api.domain.profile.repository.ProfileRepository;
import com.careerhi.api.domain.user.entity.User;
import com.careerhi.api.domain.user.repository.UserRepository;
import com.careerhi.common.exception.CustomException;
import com.careerhi.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    /**
     * 2-1. 프로필 등록 (Create Profile)
     * 기존 프로필이 있으면 삭제 후 새로 생성 (Overwrite)
     */
    @Transactional
    public void saveProfile(String email, ProfileSaveRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 기존 프로필이 있다면 삭제 (명세서의 '덮어쓰기' 정책 유지)
        profileRepository.findByUser(user)
                .ifPresent(profileRepository::delete);

        // DTO의 중첩 구조를 반영한 toEntity 호출
        Profile profile = request.toEntity(user);
        profileRepository.save(profile);
    }

    /**
     * 2-2. 프로필 조회 (Get Profile)
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));

        return ProfileResponse.from(profile);
    }

    /**
     * 2-3. 프로필 부분 수정 (Patch Profile)
     * 넘어온 필드(BasicInfo, JobInfo, SpecInfo, Portfolio)가 있을 때만 업데이트
     */
    @Transactional
    public void updateProfile(String email, ProfileSaveRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));

        // 1. 기본 정보 업데이트 (전달된 경우에만)
        if (request.getBasicInfo() != null) {
            profile.updateBasicInfo(
                    request.getBasicInfo().getName(),
                    request.getBasicInfo().getAcademicStatus(),
                    request.getBasicInfo().getSchoolName(),
                    request.getBasicInfo().getMajor(),
                    request.getBasicInfo().getEducationLevel(),
                    request.getBasicInfo().getSchoolType()
            );
        }

        // 2. 직무 정보 업데이트
        if (request.getJobInfo() != null) {
            profile.updateJobInfo(
                    request.getJobInfo().getTargetJob(),
                    request.getJobInfo().getSubRoles()
            );
        }

        // 3. 포트폴리오 정보 업데이트 (PascalCase 매핑 반영된 객체 사용)
        if (request.getPortfolio() != null) {
            profile.updatePortfolio(
                    request.getPortfolio().getUrl(),
                    request.getPortfolio().getFileName(),
                    request.getPortfolio().getFileUrl()
            );
        }

        // SpecInfo 등 나머지 필드도 필요에 따라 동일한 패턴으로 추가 가능합니다.
    }
}