package com.careerhi.api.domain.profile.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Award {
    private String contestName; // 공모전 이름
    private String awardName;   // 수상명 (금상, 입선 등)
}