package com.careerhi.api.domain.report.service;

import com.careerhi.api.domain.profile.entity.Profile;
import com.careerhi.api.domain.profile.repository.ProfileRepository;
import com.careerhi.api.domain.report.dto.AiReportResult;
import com.careerhi.api.domain.report.dto.ReportDetailResponse;
import com.careerhi.api.domain.report.dto.ReportIdResponse;
import com.careerhi.api.domain.report.entity.Report;
import com.careerhi.api.domain.report.repository.ReportRepository;
import com.careerhi.api.domain.user.entity.User;
import com.careerhi.api.domain.user.repository.UserRepository;
import com.careerhi.common.exception.CustomException;
import com.careerhi.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    // ★ 추가됨: AI 서비스와 JSON 파서
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    // [1] 분석 요청 (리포트 생성)
    @Transactional // 주의: API 호출 시간이 걸리므로 실무에서는 트랜잭션 분리나 비동기 처리를 고려하지만 현재는 유지합니다.
    public ReportIdResponse createReport(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));

        // 1. GPT에게 프로필을 주고 JSON 결과물 받아오기
        String aiJsonResult = openAiService.generateCareerReport(profile);

        try {
            // 2. 받은 JSON에서 matchRate(점수)만 빼내기 위해 파싱
            AiReportResult parsedResult = objectMapper.readValue(aiJsonResult, AiReportResult.class);

            // 3. DB에 리포트 저장 (JSON 원본도 함께 저장)
            Report report = Report.builder()
                    .user(user)
                    .targetJob(profile.getTargetJob().name())
                    .matchRate(parsedResult.getMatchRate())
                    .aiAnalysisJson(aiJsonResult) // ★ 통째로 저장
                    .build();

            reportRepository.save(report);
            return new ReportIdResponse(report.getId());

        } catch (Exception e) {
            throw new CustomException(ErrorCode.AI_SERVICE_ERROR);
        }
    }

    // [2] 리포트 상세 조회
    @Transactional(readOnly = true)
    public ReportDetailResponse getReportDetail(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        try {
            // 1. DB에 저장된 AI JSON 텍스트를 객체로 변환
            AiReportResult aiData = objectMapper.readValue(report.getAiAnalysisJson(), AiReportResult.class);

            // 2. 최종 응답(Response) 조립 (기본 정보 + AI 데이터)
            return ReportDetailResponse.builder()
                    .reportId(report.getId())
                    .userName(report.getUser().getName())
                    .targetJob(report.getTargetJob())
                    .matchRate(report.getMatchRate())
                    .createdAt(report.getCreatedAt() != null ? report.getCreatedAt().toString() : "")

                    // GPT가 만들어준 데이터 덮어씌우기
                    .overallComment(aiData.getOverallComment())
                    .certificateAnalysis(aiData.getCertificateAnalysis())
                    .awardAnalysis(aiData.getAwardAnalysis())
                    .skillGap(aiData.getSkillGap())
                    .portfolioAnalysis(aiData.getPortfolioAnalysis())
                    .build();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}










//package com.careerhi.api.domain.report.service;
//
//import com.careerhi.api.domain.profile.entity.Profile;
//import com.careerhi.api.domain.profile.repository.ProfileRepository;
//import com.careerhi.api.domain.report.dto.ReportDetailResponse;
//import com.careerhi.api.domain.report.dto.ReportIdResponse;
//import com.careerhi.api.domain.report.entity.Report;
//import com.careerhi.api.domain.report.repository.ReportRepository;
//import com.careerhi.api.domain.user.entity.User;
//import com.careerhi.api.domain.user.repository.UserRepository;
//import com.careerhi.common.exception.CustomException;
//import com.careerhi.common.exception.ErrorCode;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ReportService {
//
//    private final ReportRepository reportRepository;
//    private final ProfileRepository profileRepository;
//    private final UserRepository userRepository;
//
//    // [1] 분석 요청 (리포트 생성)
//    @Transactional
//    public ReportIdResponse createReport(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//
//        Profile profile = profileRepository.findByUser(user)
//                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));
//
//        int score = calculateMatchRate(profile);
//
//        Report report = Report.builder()
//                .user(user)
//                .targetJob(profile.getTargetJob().name())
//                .matchRate(score)
//                .build();
//
//        reportRepository.save(report);
//
//        return new ReportIdResponse(report.getId());
//    }
//
//    // [2] 리포트 상세 조회 (수정됨!)
//    @Transactional(readOnly = true)
//    public ReportDetailResponse getReportDetail(Long reportId) {
//        Report report = reportRepository.findById(reportId)
//                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));
//
//        String userName = report.getUser().getName();
//
//        return ReportDetailResponse.builder()
//                .reportId(report.getId())
//                .userName(userName)
//                .targetJob(report.getTargetJob())
//                .matchRate(report.getMatchRate())
//                // 혹시 모를 Null 에러 방지용 삼항 연산자 추가
//                .createdAt(report.getCreatedAt() != null ? report.getCreatedAt().toString() : "2026-02-18")
//
//                // [추가] 총평
//                .overallComment("최근 데이터 엔지니어가 많이 공고에서 요구하는 스펙을 일정 수준 가졌지만, 다른 경쟁자들에 비해 경쟁력이 약해요. 서류 단계에서 불합격할 확률이 높은 상태에요. 아래에서 Career-Hi가 상세히 짚어줄 테니 부족한 필수 스택을 확인하고 꿈을 향해 더 달려가 보세요!")
//
//                // 1. 자격증
//                .certificateAnalysis(ReportDetailResponse.CertificateAnalysis.builder()
//                        .title("최근 데이터 엔지니어 직군에서 요구되는 자격증이에요")
//                        .required(List.of("빅데이터분석기사", "SQLD"))
//                        .preferred(List.of("ADsP"))
//                        .industryTrend("최근 올라온 데이터 엔지니어 직군 공고 중 5곳에서 빅데이터분석기사, ADsP, SQLD 자격증을 요구하고 있어요. 이 세 가지 자격증을 요구하는 기업 중에는 카카오 등 국내 주요 대기업이 포함되어 있어요.")
//                        .coaching("이 세 가지 자격증이 모두 필요한 건 아니에요. " + userName + " 님은 이미 ADsP 자격증을 취득하셨으니 SQLD 취득을 추천해요. 두 자격증만으로 충분히 경쟁력이 생길 거예요.")
//                        .build())
//
//                // 2. 수상 경력
//                .awardAnalysis(ReportDetailResponse.AwardAnalysis.builder()
//                        .title(userName + " 님의 수상 경력은 현재 취업시장에서 불리해요")
//                        .charts(List.of(
//                                ReportDetailResponse.ChartData.builder().label("직무 관련 수상 경력 우대").userPercent(70).otherPercent(30).build(),
//                                ReportDetailResponse.ChartData.builder().label("교외 활동 우대").userPercent(62).otherPercent(38).build()
//                        ))
//                        .industryTrend(ReportDetailResponse.TextSection.builder()
//                                .summary("데이터 엔지니어 직군 공고를 분석한 결과 교외 대회에서 수상한 경험이 중요한 스펙으로 작용하고 있어요. 우대하는 주요 조건은 다음과 같아요.")
//                                .details(List.of("2년 이내 수상한 경력일 것", "교내가 아닌 외부 공모전일 것"))
//                                .build())
//                        .coaching(ReportDetailResponse.TextSection.builder()
//                                .summary(userName + " 님의 수상 경력은 모두 교내로 분류되어 있어 상대적으로 메리트가 약해요. 자격증 취득을 통해 직무 관련 지식을 높이고 외부 공모전 당선 확률을 높이는 걸 추천 할게요. 또한 개인이 수상한 경험보다 팀을 이뤄 협업한 경험이 필요해요. 팀원 협업과 수상 경력을 통해 채울 수 있는 루트를 찾아드릴게요.")
//                                .details(List.of("커리큘럼에 팀 대회가 포함되어있는 대외활동, 부트캠프 참여하기", "교외 공모전 팀 빌딩 플랫폼 활용하기"))
//                                .build())
//                        .build())
//
//                // 3. 필수 스택
//                .skillGap(ReportDetailResponse.SkillGap.builder()
//                        .title("필수 스택, 어디까지 달성하셨나요?")
//                        .items(List.of(
//                                ReportDetailResponse.SkillGapItem.builder()
//                                        .badgeTitle("TOEIC")
//                                        .badgeValue("914/850")
//                                        .isAchieved(true)
//                                        .contentTitle("어학 성적")
//                                        .contentDescription("데이터 엔지니어 직군 필수 어학 성적을 달성했어요. 하지만 만료 기한에 주의하고, 실무 자격증을 딸 수 있도록 꾸준히 학습으로 남겨두는 것이 좋아요.")
//                                        .build()
//                        ))
//                        .build())
//
//                // 4. 포트폴리오
//                .portfolioAnalysis(ReportDetailResponse.PortfolioAnalysis.builder()
//                        .title(userName + " 님의 포트폴리오를 분석했어요")
//                        .isPositive(false)
//                        .analysisResult(userName + " 님의 사용 언어인 C언어의 비중이 부실해요. 또한 협업 경험 없이 개인의 스터디 내용 위주로 구성되어 있어 실무 경험이 잘 나타나지 않았어요. 전체적인 분량이 부족하며, 형식을 준수하는 것이 중요해요. 요즘 기업에서 요구하는 파일 형식과 달라 좋은 첫인상을 주기 힘들어 보여요.")
//                        .feedbackList(List.of("C언어 사용 프로젝트 추가", "협업 경험 중심", "분량 00000자로 증량", "파일 형식 준수 (zip, pdf)"))
//                        .build())
//                .build();
//    }
//
//    private int calculateMatchRate(Profile profile) {
//        int score = 0;
//        if (!profile.getCertificates().isEmpty()) score += profile.getCertificates().size() * 15;
//        if (!profile.getLanguageTests().isEmpty()) score += profile.getLanguageTests().size() * 10;
//        if (!profile.getAwards().isEmpty()) score += profile.getAwards().size() * 20;
//
//        return Math.min(score, 100);
//    }
//}