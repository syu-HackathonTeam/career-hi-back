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
    private String portfolioUrl;

    @Getter
    @Builder
    public static class BasicInfo {
        private String name;
        private AcademicStatus academicStatus;
        private String schoolName;
        private String major;
        private String grade;
        private String semester;
    }

    @Getter
    @Builder
    public static class JobInfo {
        private JobCategory targetJob;
        private List<String> subRoles;
    }

    @Getter
    @Builder
    public static class SpecInfo {
        private List<String> certificates;
        private List<String> codingLanguages;
        private List<LanguageDto> languageTests;
        private List<AwardDto> awards;
    }

    @Getter
    @Builder
    public static class LanguageDto {
        private String testName;
        private String score;
        private String grade;
    }

    @Getter
    @Builder
    public static class AwardDto {
        private String contestName;
        private String awardName;
    }

    // Entity -> DTO 변환 메서드 (Factory Method)
    public static ProfileResponse from(Profile profile) {
        return ProfileResponse.builder()
                .basicInfo(BasicInfo.builder()
                        .name(profile.getName())
                        .academicStatus(profile.getAcademicStatus())
                        .schoolName(profile.getSchoolName())
                        .major(profile.getMajor())
                        .grade(profile.getGrade())
                        .semester(profile.getSemester())
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
                .portfolioUrl(profile.getPortfolioUrl())
                .build();
    }
}