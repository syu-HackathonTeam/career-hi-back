package com.careerhi.api.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse {
    private Integer currentPage;
    private Integer totalPages;
    private Long totalElements;
}