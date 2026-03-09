package com.careerhi.api.domain.user.service;

import com.careerhi.api.domain.user.dto.UserResponse;
import com.careerhi.api.domain.user.entity.User;
import com.careerhi.api.domain.user.repository.UserRepository;
import com.careerhi.common.exception.CustomException;
import com.careerhi.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.builder()
                .userId(user.getId())
                .userName(user.getName())
                .email(user.getEmail())
                .build();
    }
}