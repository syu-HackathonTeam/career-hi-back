package com.careerhi.collector.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class CategorizerService {

    // 우리가 원하는 6개 표준 카테고리 정의
    private final String CAT_BACKEND = "백엔드 개발";
    private final String CAT_FRONTEND = "프론트엔드 개발";
    private final String CAT_DATA_ANALYSIS = "데이터 분석";
    private final String CAT_DATA_ENGINEER = "데이터 엔지니어";
    private final String CAT_SECURITY = "보안 컨설팅";
    private final String CAT_PM = "개발 PM";

    public String categorize(String rawText, String skills) {
        String target = (rawText + " " + skills).toUpperCase();

        if (matches(target, "보안", "SECURITY", "해킹", "CISO", "정보보호", "모의해킹")) {
            return CAT_SECURITY;
        }

        if (matches(target, "데이터 엔지니어", "DATA ENGINEER", "ETL", "HADOOP", "SPARK", "HIVE", "PIPELINE")) {
            return CAT_DATA_ENGINEER;
        }

        if (matches(target, "데이터 분석", "DATA ANALYST", "DA", "통계", "머신러닝", "PYTHON", "R", "SQL", "딥러닝", "AI")) {
            return CAT_DATA_ANALYSIS;
        }

        if (matches(target, "프론트엔드", "FRONTEND", "FRONT-END", "REACT", "VUE", "ANGULAR", "JAVASCRIPT", "TYPESCRIPT", "HTML", "CSS", "퍼블리셔")) {
            return CAT_FRONTEND;
        }

        if (matches(target, "백엔드", "BACKEND", "BACK-END", "SERVER", "서버", "JAVA", "SPRING", "NODE", "DJANGO", "PHP", "ASP", "C#", "AWS", "SYSTEM")) {
            return CAT_BACKEND;
        }

        if (matches(target, "PM", "PO", "PROJECT MANAGER", "기획", "PRODUCT OWNER", "매니저", "PL", "LEADER")) {
            return CAT_PM;
        }

        return "기타 개발";
    }

    private boolean matches(String target, String... keywords) {
        return Arrays.stream(keywords).anyMatch(target::contains);
    }
}