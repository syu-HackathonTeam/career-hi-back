package com.careerhi.api.domain.profile.dto;

import com.careerhi.api.domain.profile.entity.*;
import com.careerhi.api.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 유저의 프로필 저장 요청을 받는 DTO
 * 필수값 검증(@Valid) 및 중첩 구조 반영
 */
@Getter
@NoArgsConstructor
public class ProfileSaveRequest {

    @Valid @NotNull(message = "기본 정보는 필수입니다.")
    private BasicInfo basicInfo;

    @Valid @NotNull(message = "직무 정보는 필수입니다.")
    private JobInfo jobInfo;

    @Valid @NotNull(message = "스펙 정보는 필수입니다.")
    private SpecInfo specInfo;

    @Valid
    private ProfileResponse.PortfolioInfo portfolio;

    @Getter @NoArgsConstructor
    public static class BasicInfo {
        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        @NotNull(message = "학적 상태는 필수입니다.")
        private AcademicStatus academicStatus;

        private String schoolName;
        private String major;

        @NotBlank(message = "최종 학력 수준은 필수입니다.")
        private String educationLevel;

        private String schoolType;
    }

    @Getter @NoArgsConstructor
    public static class JobInfo {
        @NotNull(message = "희망 직군은 필수입니다.")
        private JobCategory targetJob;

        @NotEmpty(message = "세부 직무를 최소 하나 이상 선택해주세요.")
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
    public static class PortfolioInfo {
        @JsonProperty("Url")      // JSON의 'Url'을 자바의 'url'로 매핑
        private String url;
        @JsonProperty("FileName")
        private String fileName;
        @JsonProperty("FileUrl")
        private String fileUrl;
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

    /**
     * DTO -> Entity 변환 메서드
     * 신규 필드인 portfolioFileUrl과 educationLevel 등을 모두 매핑해야 합니다.
     */
    public Profile toEntity(User user) {
        return Profile.builder()
                .user(user)
                .name(this.basicInfo.getName())
                .academicStatus(this.basicInfo.getAcademicStatus())
                .schoolName(this.basicInfo.getSchoolName())
                .major(this.basicInfo.getMajor())
                .educationLevel(this.basicInfo.getEducationLevel())
                .schoolType(this.basicInfo.getSchoolType())
                .targetJob(this.jobInfo.getTargetJob())
                .subRoles(this.jobInfo.getSubRoles())
                .certificates(this.specInfo.getCertificates())
                .codingLanguages(this.specInfo.getCodingLanguages())
                .languageTests(this.specInfo.getLanguageTests().stream()
                        .map(l -> new LanguageTest(l.getTestName(), l.getScore(), l.getGrade()))
                        .collect(Collectors.toList()))
                .awards(this.specInfo.getAwards().stream()
                        .map(a -> new Award(a.getContestName(), a.getAwardName()))
                        .collect(Collectors.toList()))
                .portfolioUrl(this.portfolio != null ? this.portfolio.getUrl() : null)
                .portfolioFileUrl(this.portfolio != null ? this.portfolio.getFileUrl() : null)
                .portfolioFileName(this.portfolio != null ? this.portfolio.getFileName() : null)
                .build();
    }
}