package com.careerhi.api.domain.profile.entity;

import com.careerhi.api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 유저의 프로필인지 연결 (1:1 관계)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // [기본 정보]
    @Column(nullable = false)
    private String name; // 이름

    @Enumerated(EnumType.STRING)
    private AcademicStatus academicStatus; // 학적 상태 (재학/졸업 등)

    private String schoolName; // 학교명
    private String major; // 전공
    private String grade; // 학년
    private String semester; // 학기

    // [직업 정보]
    @Enumerated(EnumType.STRING)
    private JobCategory targetJob; // 희망 직군 (IT/데이터 등)

    // 세부 직무는 여러 개 선택 가능 (예: 백엔드, 데이터엔지니어) -> 별도 테이블로 저장
    @ElementCollection
    @CollectionTable(name = "profile_sub_roles", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "role_name")
    private List<String> subRoles = new ArrayList<>();

    // [스펙 정보]
    // 자격증 (단순 문자열 리스트)
    @ElementCollection
    @CollectionTable(name = "profile_certificates", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "certificate_name")
    private List<String> certificates = new ArrayList<>();

    // 사용 언어 (Java, Python 등)
    @ElementCollection
    @CollectionTable(name = "profile_coding_languages", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "language_name")
    private List<String> codingLanguages = new ArrayList<>();

    // 어학 성적 (복잡한 정보라 별도 엔티티 분리 안 하고 Embedded로 처리 가능하지만, 관리를 위해 ElementCollection 사용)
    @ElementCollection
    @CollectionTable(name = "profile_language_tests", joinColumns = @JoinColumn(name = "profile_id"))
    private List<LanguageTest> languageTests = new ArrayList<>();

    // 수상 내역
    @ElementCollection
    @CollectionTable(name = "profile_awards", joinColumns = @JoinColumn(name = "profile_id"))
    private List<Award> awards = new ArrayList<>();

    // [포트폴리오]
    private String portfolioUrl; // 깃허브 등 링크
    private String portfolioFileUrl; // 업로드된 파일 경로

    @Builder
    public Profile(User user, String name, AcademicStatus academicStatus, String schoolName,
                   String major, String grade, String semester, JobCategory targetJob,
                   List<String> subRoles, List<String> certificates, List<String> codingLanguages,
                   List<LanguageTest> languageTests, List<Award> awards,
                   String portfolioUrl, String portfolioFileUrl) {
        this.user = user;
        this.name = name;
        this.academicStatus = academicStatus;
        this.schoolName = schoolName;
        this.major = major;
        this.grade = grade;
        this.semester = semester;
        this.targetJob = targetJob;
        this.subRoles = subRoles;
        this.certificates = certificates;
        this.codingLanguages = codingLanguages;
        this.languageTests = languageTests;
        this.awards = awards;
        this.portfolioUrl = portfolioUrl;
        this.portfolioFileUrl = portfolioFileUrl;
    }

    // 수정 메서드 (나중에 사용)
    public void updatePortfolioFile(String fileUrl) {
        this.portfolioFileUrl = fileUrl;
    }
}