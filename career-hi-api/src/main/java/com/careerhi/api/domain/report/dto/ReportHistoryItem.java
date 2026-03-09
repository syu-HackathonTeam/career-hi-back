package com.careerhi.api.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportHistoryItem {
    private Long reportId;
    private String title;
    private String date;
    private Integer matchRate;
    private Boolean canViewSpec;
    private Boolean canViewReport;
}