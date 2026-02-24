package com.careerhi.api.domain.profile.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable // Profile 테이블의 하위 테이블로 들어감
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LanguageTest {
    private String testName; // 시험명 (OPIc, TOEIC)
    private String score;    // 점수 (900)
    private String grade;    // 등급 (IM2, AL) - 점수나 등급 중 하나만 있을 수 있음
}