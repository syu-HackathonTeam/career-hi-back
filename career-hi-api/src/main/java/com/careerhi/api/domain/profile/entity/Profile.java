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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // [기본 정보]
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private AcademicStatus academicStatus;

    private String schoolName;
    private String major;
    private String educationLevel;
    private String schoolType;

    // [직업 정보]
    @Enumerated(EnumType.STRING)
    private JobCategory targetJob;

    @ElementCollection
    @CollectionTable(name = "profile_sub_roles", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "role_name")
    private List<String> subRoles = new ArrayList<>();

    // [스펙 정보]
    @ElementCollection
    @CollectionTable(name = "profile_certificates", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "certificate_name")
    private List<String> certificates = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_coding_languages", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "language_name")
    private List<String> codingLanguages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_language_tests", joinColumns = @JoinColumn(name = "profile_id"))
    private List<LanguageTest> languageTests = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_awards", joinColumns = @JoinColumn(name = "profile_id"))
    private List<Award> awards = new ArrayList<>();

    // [포트폴리오]
    private String portfolioUrl;
    private String portfolioFileUrl;
    private String portfolioFileName;

    @Builder
    public Profile(User user, String name, AcademicStatus academicStatus, String schoolName,
                   String major, String educationLevel, String schoolType, JobCategory targetJob,
                   List<String> subRoles, List<String> certificates, List<String> codingLanguages,
                   List<LanguageTest> languageTests, List<Award> awards,
                   String portfolioUrl, String portfolioFileUrl, String portfolioFileName) {
        this.user = user;
        this.name = name;
        this.academicStatus = academicStatus;
        this.schoolName = schoolName;
        this.major = major;
        this.educationLevel = educationLevel;
        this.schoolType = schoolType;
        this.targetJob = targetJob;
        this.subRoles = subRoles;
        this.certificates = certificates;
        this.codingLanguages = codingLanguages;
        this.languageTests = languageTests;
        this.awards = awards;
        this.portfolioUrl = portfolioUrl;
        this.portfolioFileUrl = portfolioFileUrl;
        this.portfolioFileName = portfolioFileName;
    }

    // --- 비즈니스 로직 (업데이트 메서드) ---

    /**
     * 기본 정보 업데이트
     */
    public void updateBasicInfo(String name, AcademicStatus academicStatus, String schoolName,
                                String major, String educationLevel, String schoolType) {
        if (name != null) this.name = name;
        if (academicStatus != null) this.academicStatus = academicStatus;
        if (schoolName != null) this.schoolName = schoolName;
        if (major != null) this.major = major;
        if (educationLevel != null) this.educationLevel = educationLevel;
        if (schoolType != null) this.schoolType = schoolType;
    }

    /**
     * 직무 정보 업데이트
     */
    public void updateJobInfo(JobCategory targetJob, List<String> subRoles) {
        if (targetJob != null) this.targetJob = targetJob;
        if (subRoles != null) {
            this.subRoles.clear(); // 기존 리스트를 비우고
            this.subRoles.addAll(subRoles); // 새로운 리스트로 교체
        }
    }

    /**
     * 포트폴리오 업데이트
     */
    public void updatePortfolio(String url, String fileName, String fileUrl) {
        if (url != null) this.portfolioUrl = url;
        if (fileName != null) this.portfolioFileName = fileName;
        if (fileUrl != null) this.portfolioFileUrl = fileUrl;
    }
}