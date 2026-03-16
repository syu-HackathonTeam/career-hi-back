package com.careerhi.api.domain.profile.dto;

import com.careerhi.api.domain.profile.entity.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProfileResponse {

    private BasicInfo basicInfo;
    private JobInfo jobInfo;
    private SpecInfo specInfo;
    private PortfolioInfo portfolio; // ★ 명세서 2-2에 맞춰 객체화

    @Getter @Builder
    public static class BasicInfo {
        private String name;
        private AcademicStatus academicStatus;
        private String schoolName;
        private String major;
        private String educationLevel;
        private String schoolType;
    }

    @Getter @Builder
    public static class JobInfo {
        private JobCategory targetJob;
        private List<String> subRoles;
    }

    @Getter @Builder
    public static class SpecInfo {
        private List<String> certificates;
        private List<String> codingLanguages;
        private List<LanguageDto> languageTests;
        private List<AwardDto> awards;
    }

    @Getter
    @Builder
    public static class PortfolioInfo { // ★ 신규 추가
        private String Url;      // 명세서 표기법(대문자 시작) 준수
        private String FileName;
        private String FileUrl;
    }

    @Getter @Builder
    public static class LanguageDto {
        private String testName;
        private String score;
        private String grade;
    }

    @Getter @Builder
    public static class AwardDto {
        private String contestName;
        private String awardName;
    }

    public static ProfileResponse from(Profile profile) {
        return ProfileResponse.builder()
                .basicInfo(BasicInfo.builder()
                        .name(profile.getName())
                        .academicStatus(profile.getAcademicStatus())
                        .schoolName(profile.getSchoolName())
                        .major(profile.getMajor())
                        .educationLevel(profile.getEducationLevel())
                        .schoolType(profile.getSchoolType())
                        .build())
                .jobInfo(JobInfo.builder()
                        .targetJob(profile.getTargetJob())
                        .subRoles(profile.getSubRoles())
                        .build())
                .specInfo(SpecInfo.builder()
                        .certificates(profile.getCertificates())
                        .codingLanguages(profile.getCodingLanguages())
                        .languageTests(profile.getLanguageTests().stream()
                                .map(l -> LanguageDto.builder()
                                        .testName(l.getTestName())
                                        .score(l.getScore())
                                        .grade(l.getGrade())
                                        .build())
                                .collect(Collectors.toList()))
                        .awards(profile.getAwards().stream()
                                .map(a -> AwardDto.builder()
                                        .contestName(a.getContestName())
                                        .awardName(a.getAwardName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                // ★ 명세서 2-2의 중첩 구조 반영
                .portfolio(PortfolioInfo.builder()
                        .Url(profile.getPortfolioUrl())
                        .FileName(profile.getPortfolioFileName()) // 엔티티에서 가져옴
                        .FileUrl(profile.getPortfolioFileUrl())
                        .build())
                .build();
    }
}