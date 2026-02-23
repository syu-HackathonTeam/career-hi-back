package com.careerhi.api.domain.profile.repository;

import com.careerhi.api.domain.profile.entity.Profile;
import com.careerhi.api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // 특정 유저의 프로필이 이미 있는지 확인하기 위해 필요
    Optional<Profile> findByUser(User user);
}