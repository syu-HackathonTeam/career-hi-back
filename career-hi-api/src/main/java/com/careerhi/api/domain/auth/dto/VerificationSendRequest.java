package com.careerhi.api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record VerificationSendRequest(
        @NotBlank String phoneNumber,
        @NotBlank String type // "SIGNUP" 또는 "FIND_PW"
) {}
