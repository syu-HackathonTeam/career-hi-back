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

    @Transactional
    public void saveProfile(String email, ProfileSaveRequest request) {
        // 1. 로그인한 유저 정보 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 기존 프로필이 있다면 삭제 (덮어쓰기 로직)
        // (간단하게 구현하기 위해 기존 거 지우고 새로 저장하는 방식 선택)
        profileRepository.findByUser(user)
                .ifPresent(profileRepository::delete);

        // 3. DTO를 Entity로 변환
        Profile profile = request.toEntity(user);

        // 4. 저장
        profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND)); // 또는 PROFILE_NOT_FOUND 에러 추가 권장

        return ProfileResponse.from(profile);
    }
}