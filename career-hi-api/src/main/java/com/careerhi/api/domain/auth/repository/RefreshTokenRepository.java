package com.careerhi.api.domain.auth.repository;

import com.careerhi.api.domain.auth.entity.RefreshToken; // 본인의 엔티티 경로
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByEmail(String email); // 이 메서드가 있어야 서비스에서 호출 가능해요.
}