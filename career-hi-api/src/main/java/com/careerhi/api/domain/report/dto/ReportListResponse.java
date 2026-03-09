package com.careerhi.api.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportListResponse {
    private List<GrowthPoint> growthChart;
    private String chartAnalysis;
    private List<ReportHistoryItem> reportHistory;
    private PaginationResponse pagination;
}