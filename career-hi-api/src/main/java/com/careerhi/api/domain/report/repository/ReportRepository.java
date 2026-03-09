package com.careerhi.api.domain.report.repository;

import com.careerhi.api.domain.report.entity.Report;
import com.careerhi.api.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 히스토리 목록 조회용 (최신순 정렬)
    List<Report> findByUserOrderByCreatedAtDesc(User user);

    // 페이징 조회 지원 (서비스에서 Page\<Report\> 반환 시 사용)
    Page<Report> findAllByUser(User user, Pageable pageable);

    // 정렬된 전체 리스트 조회 지원 (성장 차트 같은 전체 조회 시 사용)
    List<Report> findAllByUser(User user, Sort sort);
}