package com.careerhi.api.domain.report.entity;

import com.careerhi.api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String targetJob;

    @Column(nullable = false)
    private Integer matchRate;

    // ★ 추가됨: AI가 분석한 JSON 데이터를 통째로 저장하는 컬럼 (길이가 길 수 있으므로 TEXT 지정)
    @Column(columnDefinition = "TEXT")
    private String aiAnalysisJson;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdAt;

    @Builder
    public Report(User user, String targetJob, Integer matchRate, String aiAnalysisJson) {
        this.user = user;
        this.targetJob = targetJob;
        this.matchRate = matchRate;
        this.aiAnalysisJson = aiAnalysisJson; // 저장 추가
    }
}