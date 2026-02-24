package com.careerhi.api.domain.report.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class AiReportResult {
    // GPT가 이 형식 그대로 JSON을 만들어 주도록 명령할 것입니다.
    private int matchRate;
    private String overallComment;

    private ReportDetailResponse.CertificateAnalysis certificateAnalysis;
    private ReportDetailResponse.AwardAnalysis awardAnalysis;
    private ReportDetailResponse.SkillGap skillGap;
    private ReportDetailResponse.PortfolioAnalysis portfolioAnalysis;
}