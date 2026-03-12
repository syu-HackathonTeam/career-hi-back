package com.careerhi.api.domain.report.service;

import com.careerhi.api.domain.profile.entity.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateCareerReport(Profile profile) {
        String url = "https://api.openai.com/v1/chat/completions";

        // 1. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 2. GPT에게 내릴 프롬프트(명령어) 작성
        String prompt = buildPrompt(profile);

        // 3. 요청 바디 구성 (최신이고 가성비가 좋은 gpt-4o-mini 모델 사용)
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", List.of(message));
        // ★ 중요: 무조건 JSON 형태로만 대답하라고 강제하는 옵션
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // 4. OpenAI API 호출
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            // 5. 결과 파싱해서 JSON String만 빼내기
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> responseMessage = (Map<String, Object>) choices.get(0).get("message");

            log.info("GPT Response: {}", responseMessage.get("content").toString());
            return responseMessage.get("content").toString();

        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생", e);
            throw new RuntimeException("AI 분석에 실패했습니다.");
        }
    }

    private String buildPrompt(Profile profile) {
        // 1. 포트폴리오 유무에 따른 가이드라인 생성 (환각 방지 핵심 로직)
        String portfolioContext;
        if (profile.getPortfolioUrl() == null || profile.getPortfolioUrl().isBlank()) {
            portfolioContext = "- 포트폴리오: [제출되지 않음]\n" +
                    "  (주의: 사용자가 포트폴리오를 제출하지 않았으므로, 분석 결과에 '제출된 포트폴리오가 없습니다'라고 명시하고 일반적인 준비 가이드를 제공하세요.)";
        } else {
            portfolioContext = "- 포트폴리오 URL: " + profile.getPortfolioUrl();
        }

        // 2. 실제 리스트 데이터들을 문자열로 변환하여 주입
        return "당신은 IT 취업 전문 1타 커리어 컨설턴트입니다. 다음 사용자의 상세 스펙을 바탕으로 '취업 로드맵 리포트'를 작성해주세요.\n\n" +
                "[사용자 상세 스펙]\n" +
                "- 이름: " + profile.getName() + "\n" +
                "- 목표 직군: " + profile.getTargetJob().name() + " (세부역할: " + profile.getSubRoles().toString() + ")\n" +
                "- 보유 자격증: " + (profile.getCertificates().isEmpty() ? "없음" : profile.getCertificates().toString()) + "\n" +
                "- 보유 기술 스택: " + (profile.getCodingLanguages().isEmpty() ? "없음" : profile.getCodingLanguages().toString()) + "\n" +
                "- 수상 경력: " + (profile.getAwards().isEmpty() ? "없음" : profile.getAwards().toString()) + "\n" +
                "- 어학 성적: " + (profile.getLanguageTests().isEmpty() ? "없음" : profile.getLanguageTests().toString()) + "\n" +
                portfolioContext + "\n\n" +

                "[작성 규칙]\n" +
                "1. 반드시 아래의 JSON 구조와 일치하게 응답하세요. 다른 설명은 생략합니다.\n" +
                "2. 'skillGap'의 'items'는 반드시 사용자의 기술 스택과 자격증을 모두 분석하여 **최소 4개 이상** 생성하세요.\n" +
                "3. 포트폴리오가 제출되지 않은 경우 'portfolioAnalysis'의 'analysisResult'에 반드시 '포트폴리오 정보가 부족합니다'라는 내용을 포함하세요.\n" +
                "4. 모든 분석 내용은 한국어로 작성하며, 전문적이고 신뢰감 있는 톤을 유지하세요.\n\n" +

                "{\n" +
                "  \"matchRate\": (0~100 사이 정수),\n" +
                "  \"overallComment\": \"(3문장 이내 총평)\",\n" +
                "  \"certificateAnalysis\": {\n" +
                "    \"title\": \"최근 해당 직군에서 요구되는 자격증이에요\",\n" +
                "    \"required\": [\"필수자격증1\", \"필수자격증2\"],\n" +
                "    \"preferred\": [\"우대자격증1\"],\n" +
                "    \"industryTrend\": \"(업계 동향)\",\n" +
                "    \"coaching\": \"(맞춤형 자격증 전략)\"\n" +
                "  },\n" +
                "  \"awardAnalysis\": {\n" +
                "    \"title\": \"수상 경력 분석결과\",\n" +
                "    \"charts\": [{\"label\": \"직무 관련 수상 우대\", \"userPercent\": 60, \"otherPercent\": 40}],\n" +
                "    \"industryTrend\": {\"summary\": \"...\", \"details\": [\"동향1\", \"동향2\"]},\n" +
                "    \"coaching\": {\"summary\": \"...\", \"details\": [\"제안1\", \"제안2\"]}\n" +
                "  },\n" +
                "  \"skillGap\": {\n" +
                "    \"title\": \"필수 스택 달성도\",\n" +
                "    \"items\": [\n" +
                "      {\n" +
                "        \"badgeTitle\": \"기술/자격증명\",\n" +
                "        \"badgeValue\": \"수준(상/중/하)\",\n" +
                "        \"isAchieved\": (보유여부 true/false),\n" +
                "        \"contentTitle\": \"핵심 역량명\",\n" +
                "        \"contentDescription\": \"설명\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"portfolioAnalysis\": {\n" +
                "    \"title\": \"포트폴리오 분석\",\n" +
                "    \"isPositive\": (포트폴리오 유무 및 퀄리티에 따른 true/false),\n" +
                "    \"analysisResult\": \"(결과 분석 내용)\",\n" +
                "    \"feedbackList\": [\"보완점1\", \"보완점2\"]\n" +
                "  }\n" +
                "}";
    }
}