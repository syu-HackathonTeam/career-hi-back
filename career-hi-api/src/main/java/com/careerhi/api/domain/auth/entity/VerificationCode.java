package com.careerhi.api.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String authCode;

    @Column(nullable = false)
    private String type; // "SIGNUP" 또는 "FIND_PW"

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // 비즈니스 로직: 만료 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}