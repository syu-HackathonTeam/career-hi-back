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

    @NotNull private BasicInfo basicInfo;
    @NotNull private JobInfo jobInfo;
    @NotNull private SpecInfo specInfo;
    private String portfolioUrl;

    // --- 내부 클래스로 구조화 ---
    @Getter @NoArgsConstructor
    public static class BasicInfo {
        @NotBlank private String name;
        @NotNull private AcademicStatus academicStatus;
        @NotBlank private String schoolName;
        @NotBlank private String major;

        // [수정] 기존 grade, semester 삭제 -> 신규 필드 추가
        // 필수 값이라면 @NotBlank, 선택이라면 삭제 가능. 우선은 필수로 생각.
        @NotBlank
        private String educationLevel;

        @NotBlank
        private String schoolType;
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
                .name(this.basicInfo.getName()) // getter 사용 권장
                .academicStatus(this.basicInfo.getAcademicStatus())
                .schoolName(this.basicInfo.getSchoolName())
                .major(this.basicInfo.getMajor())
                // [수정] 빌더에 새 필드 주입
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