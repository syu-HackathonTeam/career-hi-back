package com.careerhi.api.domain.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReportDetailResponse {

    private Long reportId;
    private String userName;
    private String targetJob;
    private Integer matchRate;
    private String createdAt;

    // [추가됨] 총평 코멘트
    private String overallComment;

    private CertificateAnalysis certificateAnalysis;
    private AwardAnalysis awardAnalysis;
    private SkillGap skillGap;
    private PortfolioAnalysis portfolioAnalysis;

    // --- 내부 클래스 ---

    @Getter @Builder
    public static class CertificateAnalysis {
        private String title;
        private List<String> required;
        private List<String> preferred;
        private String industryTrend; // 업계 동향
        private String coaching;      // 방향성 코칭
    }

    @Getter @Builder
    public static class AwardAnalysis {
        private String title;
        private List<ChartData> charts; // 막대 그래프 데이터
        private TextSection industryTrend; // 업계 동향 (리스트 포함)
        private TextSection coaching;      // 방향성 코칭 (리스트 포함)
    }

    @Getter @Builder
    public static class ChartData {
        private String label;
        private int userPercent;
        private int otherPercent;
    }

    // 제목(summary)과 불릿 포인트 리스트(details)를 묶는 공통 클래스
    @Getter @Builder
    public static class TextSection {
        private String summary;
        private List<String> details;
    }

    @Getter @Builder
    public static class SkillGap {
        private String title;
        private List<SkillGapItem> items; // 부족한 스택 리스트
    }

    @Getter @Builder
    public static class SkillGapItem {
        private String badgeTitle;    // 예: TOEIC
        private String badgeValue;    // 예: 914/850
        private boolean isAchieved;   // 달성 여부 (초록색/회색 뱃지)
        private String contentTitle;  // 예: 어학 성적
        private String contentDescription; // 상세 설명
    }

    @Getter @Builder
    public static class PortfolioAnalysis {
        private String title;
        private boolean isPositive;
        private String analysisResult;     // 분석 결과
        private List<String> feedbackList; // 피드백 불릿 포인트
    }
}