package com.careerhi.api.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users") // H2 DB에서 'user'는 예약어일 수 있어 users로 명명
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 보호
@EntityListeners(AuditingEntityListener.class) // 생성/수정 시간 자동 관리
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // 암호화되어 저장됨

    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean marketingAgreed;

    // 추후 Role(권한) 추가 가능 (예: ROLE_USER, ROLE_ADMIN)

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public User(String name, String email, String password, String phoneNumber, Boolean marketingAgreed) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.marketingAgreed = marketingAgreed;
    }
}