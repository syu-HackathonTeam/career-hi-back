package com.careerhi.api.domain.report.repository;

import com.careerhi.api.domain.report.entity.Report;
import com.careerhi.api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 히스토리 목록 조회용 (최신순 정렬)
    List<Report> findByUserOrderByCreatedAtDesc(User user);
}