package com.careerhi.api.domain.report.controller;

import com.careerhi.api.domain.report.dto.ReportDetailResponse;
import com.careerhi.api.domain.report.dto.ReportIdResponse;
import com.careerhi.api.domain.report.dto.ReportListResponse;
import com.careerhi.api.domain.report.service.ReportService;
import com.careerhi.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4. Report", description = "AI 기반 분석 리포트 및 로드맵 API")
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 1. AI 분석 요청 (리포트 생성)
    @Operation(summary = "AI 분석 요청", description = "저장된 프로필 기반으로 AI 로드맵 리포트를 생성합니다.")
    @PostMapping("/analyze")
    public ApiResponse<ReportIdResponse> analyzeProfile(@AuthenticationPrincipal UserDetails userDetails) {
        ReportIdResponse response = reportService.createReport(userDetails.getUsername());
        return ApiResponse.success("로드맵 생성이 완료되었습니다.", response);
    }

    // 2. 리포트 상세 조회
    @Operation(summary = "리포트 상세 조회", description = "특정 리포트 ID의 전체 분석 내용을 조회합니다.")
    @GetMapping("/{reportId}")
    public ApiResponse<ReportDetailResponse> getReportDetail(@PathVariable Long reportId) {
        ReportDetailResponse response = reportService.getReportDetail(reportId);
        return ApiResponse.success("리포트 상세 조회 성공", response);
    }

    @Operation(summary = "리포트 목록 조회", description = "본인이 생성한 모든 리포트 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ApiResponse<ReportListResponse> getReportList(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "한 페이지당 조회 개수") @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        ReportListResponse response = reportService.getReportList(userDetails.getUsername(), page, size);
        return ApiResponse.success("리포트 목록 조회 성공", response);
    }

    @Operation(summary = "리포트 삭제", description = "특정 생성 리포트를 삭제합니다.")
    @DeleteMapping("/{reportId}")
    public ApiResponse<Void> deleteReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long reportId
    ) {
        reportService.deleteReport(userDetails.getUsername(), reportId);
        return ApiResponse.success("리포트가 성공적으로 삭제되었습니다.");
    }
}