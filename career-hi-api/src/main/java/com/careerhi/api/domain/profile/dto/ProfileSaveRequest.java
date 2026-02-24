package com.careerhi.api.domain.profile.dto;

import com.careerhi.api.domain.profile.entity.*;
import com.careerhi.api.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ProfileSaveRequest {

    // 1. 기본 정보
    @NotNull private BasicInfo basicInfo;

    // 2. 직업 정보
    @NotNull private JobInfo jobInfo;

    // 3. 스펙 정보
    @NotNull private SpecInfo specInfo;

    // 4. 포트폴리오
    private String portfolioUrl;

    // --- 내부 클래스로 구조화 ---
    @Getter @NoArgsConstructor
    public static class BasicInfo {
        @NotBlank private String name;
        @NotNull private AcademicStatus academicStatus;
        @NotBlank private String schoolName;
        @NotBlank private String major;
        private String grade;
        private String semester;
    }

    @Getter @NoArgsConstructor
    public static class JobInfo {
        @NotNull private JobCategory targetJob;
        private List<String> subRoles;
    }

    @Getter @NoArgsConstructor
    public static class SpecInfo {
        private List<String> certificates;
        private List<LanguageDto> languageTests;
        private List<AwardDto> awards;
        private List<String> codingLanguages;
    }

    @Getter @NoArgsConstructor
    public static class LanguageDto {
        private String testName;
        private String score;
        private String grade;
    }

    @Getter @NoArgsConstructor
    public static class AwardDto {
        private String contestName;
        private String awardName;
    }

    // DTO -> Entity 변환 메서드
    public Profile toEntity(User user) {
        return Profile.builder()
                .user(user)
                .name(this.basicInfo.name)
                .academicStatus(this.basicInfo.academicStatus)
                .schoolName(this.basicInfo.schoolName)
                .major(this.basicInfo.major)
                .grade(this.basicInfo.grade)
                .semester(this.basicInfo.semester)
                .targetJob(this.jobInfo.targetJob)
                .subRoles(this.jobInfo.subRoles)
                .certificates(this.specInfo.certificates)
                .codingLanguages(this.specInfo.codingLanguages)
                .languageTests(this.specInfo.languageTests.stream()
                        .map(l -> new LanguageTest(l.testName, l.score, l.grade))
                        .collect(Collectors.toList()))
                .awards(this.specInfo.awards.stream()
                        .map(a -> new Award(a.contestName, a.awardName))
                        .collect(Collectors.toList()))
                .portfolioUrl(this.portfolioUrl)
                .build();
    }
}