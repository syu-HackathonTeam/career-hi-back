package com.careerhi.api.domain.user.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        Long userId,
        String userName,
        String email
) {}