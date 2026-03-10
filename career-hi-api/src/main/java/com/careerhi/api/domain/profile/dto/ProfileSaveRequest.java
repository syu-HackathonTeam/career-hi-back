package com.careerhi.api.domain.profile.dto;

import com.careerhi.api.domain.profile.entity.*;
import com.careerhi.api.domain.user.entity.User;
import jakarta.validation.Valid; // 중요: 내부 객체 검증을 위해 반드시 필요
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 유저의 프로필 저장 요청을 받는 DTO
 * 피그마 필수값: 이름, 학력(학력수준), 희망직군, 세부사항(subRoles)
 */
@Getter
@NoArgsConstructor
public class ProfileSaveRequest {

    // @Valid: 이 어노테이션이 있어야 BasicInfo 내부의 @NotBlank 등이 작동합니다.
    @Valid
    @NotNull(message = "기본 정보(BasicInfo)는 필수 입력 항목입니다.")
    private BasicInfo basicInfo;

    @Valid
    @NotNull(message = "직무 정보(JobInfo)는 필수 입력 항목입니다.")
    private JobInfo jobInfo;

    // SpecInfo는 필드들이 선택 사항이므로 @Valid만 붙여 내부 구조를 확인하게 합니다.
    @Valid
    @NotNull(message = "스펙 정보(SpecInfo) 객체 자체는 전달되어야 합니다. (빈 리스트 가능)")
    private SpecInfo specInfo;

    private String portfolioUrl;

    // --- 1. 기본 정보 (이름, 학력 포함) ---
    @Getter @NoArgsConstructor
    public static class BasicInfo {
        @NotBlank(message = "이름은 필수 입력값입니다.") // 피그마 필수
        private String name;

        @NotNull(message = "학적 상태(AcademicStatus)를 선택해주세요.")
        private AcademicStatus academicStatus;

        private String schoolName; // 선택 사항 (피그마 기준)
        private String major;      // 선택 사항 (피그마 기준)

        @NotBlank(message = "학력 수준(예: 대학교 재학, 졸업 등)은 필수입니다.") // 피그마 필수
        private String educationLevel;

        private String schoolType; // 선택 사항
    }

    // --- 2. 직무 정보 (희망직군, 세부사항 포함) ---
    @Getter @NoArgsConstructor
    public static class JobInfo {
        @NotNull(message = "희망 직군은 필수 선택 항목입니다.") // 피그마 필수
        private JobCategory targetJob;

        // @NotEmpty: 리스트가 null이 아니고, 크기가 0보다 커야 함을 보장합니다.
        @NotEmpty(message = "세부 역할(세부사항)은 최소 하나 이상 선택해야 합니다.") // 피그마 필수
        private List<String> subRoles;
    }

    // --- 3. 스펙 정보 (자격증, 수상 등 - 선택 사항) ---
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

    /**
     * DTO 데이터를 바탕으로 Profile 엔티티를 생성하는 빌더 메서드
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
                .portfolioUrl(this.portfolioUrl)
                .build();
    }
}