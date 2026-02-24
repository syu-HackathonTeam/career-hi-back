package com.careerhi.api.domain.profile.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AcademicStatus {
    ENROLLED("재학"),
    LEAVE("휴학"),
    GRADUATED("졸업"),
    DELAYED("졸업유예"),
    DROPOUT("중퇴");

    private final String value;

    AcademicStatus(String value) {
        this.value = value;
    }

    // [1] 서버 -> 프론트 (DB의 ENROLLED를 "재학"으로 바꿔서 응답)
    @JsonValue
    public String getValue() {
        return value;
    }

    // [2] 프론트 -> 서버 (프론트가 보낸 "재학"을 DB용 ENROLLED로 변환)
    @JsonCreator
    public static AcademicStatus from(String val) {
        for (AcademicStatus status : AcademicStatus.values()) {
            if (status.getValue().equals(val)) {
                return status;
            }
        }
        throw new IllegalArgumentException("올바르지 않은 학적 상태입니다: " + val);
    }
}