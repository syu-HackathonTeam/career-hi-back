package com.careerhi.api.domain.auth.repository;

import com.careerhi.api.domain.auth.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findTopByPhoneNumberAndTypeOrderByExpiryDateDesc(String phoneNumber, String type);

    @Modifying
    void deleteByPhoneNumberAndType(String phoneNumber, String type);
}