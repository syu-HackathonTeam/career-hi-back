package com.careerhi.api.domain.report.controller;

import com.careerhi.api.domain.report.dto.ReportDetailResponse;
import com.careerhi.api.domain.report.dto.ReportIdResponse;
import com.careerhi.api.domain.report.dto.ReportListResponse;
import com.careerhi.api.domain.report.service.ReportService;
import com.careerhi.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 1. AI 분석 요청 (리포트 생성)
    @PostMapping("/analyze")
    public ApiResponse<ReportIdResponse> analyzeProfile(@AuthenticationPrincipal UserDetails userDetails) {
        ReportIdResponse response = reportService.createReport(userDetails.getUsername());
        return ApiResponse.success("로드맵 생성이 완료되었습니다.", response);
    }

    // 2. 리포트 상세 조회
    @GetMapping("/{reportId}")
    public ApiResponse<ReportDetailResponse> getReportDetail(@PathVariable Long reportId) {
        ReportDetailResponse response = reportService.getReportDetail(reportId);
        return ApiResponse.success("리포트 상세 조회 성공", response);
    }

    @GetMapping
    public ApiResponse<ReportListResponse> getReportList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        ReportListResponse response = reportService.getReportList(userDetails.getUsername(), page, size);
        return ApiResponse.success("리포트 목록 조회 성공", response);
    }

    @DeleteMapping("/{reportId}")
    public ApiResponse<Void> deleteReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long reportId
    ) {
        reportService.deleteReport(userDetails.getUsername(), reportId);
        return ApiResponse.success("리포트가 성공적으로 삭제되었습니다.");
    }
}