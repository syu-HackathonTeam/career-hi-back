package com.careerhi.api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record VerificationCheckRequest(
        @NotBlank String phoneNumber,
        @NotBlank String authCode,
        @NotBlank String type
) {}
