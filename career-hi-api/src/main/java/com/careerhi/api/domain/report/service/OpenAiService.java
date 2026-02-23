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
        return "당신은 IT 취업 전문 1타 커리어 컨설턴트입니다. 다음 사용자의 스펙을 분석하여 취업 로드맵 리포트를 작성해주세요.\n\n" +
                "[사용자 스펙]\n" +
                "- 이름: " + profile.getName() + "\n" +
                "- 목표 직군: " + profile.getTargetJob().name() + " (" + profile.getSubRoles().toString() + ")\n" +
                "- 자격증: " + profile.getCertificates().toString() + "\n" +
                "- 어학/수상/스택 등 이력서 기반 데이터가 들어왔습니다.\n\n" +
                "반드시 아래의 JSON 구조와 일치하게 응답을 작성해야 합니다. 다른 말은 절대 추가하지 마세요. (한국어로 작성)\n" +
                "{\n" +
                "  \"matchRate\": (직무 적합도 점수 0~100 사이 정수),\n" +
                "  \"overallComment\": \"(전반적인 총평, 3문장 이내)\",\n" +
                "  \"certificateAnalysis\": {\n" +
                "    \"title\": \"최근 해당 직군에서 요구되는 자격증이에요\",\n" +
                "    \"required\": [\"필수자격증1\"],\n" +
                "    \"preferred\": [\"우대자격증1\"],\n" +
                "    \"industryTrend\": \"(해당 직군의 자격증 업계 동향)\",\n" +
                "    \"coaching\": \"(유저 맞춤형 자격증 코칭)\"\n" +
                "  },\n" +
                "  \"awardAnalysis\": {\n" +
                "    \"title\": \"수상 경력 분석결과\",\n" +
                "    \"charts\": [{\"label\": \"직무 관련 수상 우대\", \"userPercent\": 60, \"otherPercent\": 40}],\n" +
                "    \"industryTrend\": {\"summary\": \"...\", \"details\": [\"요건1\", \"요건2\"]},\n" +
                "    \"coaching\": {\"summary\": \"...\", \"details\": [\"액션1\", \"액션2\"]}\n" +
                "  },\n" +
                "  \"skillGap\": {\n" +
                "    \"title\": \"필수 스택 달성도\",\n" +
                "    \"items\": [{\"badgeTitle\": \"기술/어학명\", \"badgeValue\": \"점수또는등급\", \"isAchieved\": true, \"contentTitle\": \"항목명\", \"contentDescription\": \"설명\"}]\n" +
                "  },\n" +
                "  \"portfolioAnalysis\": {\n" +
                "    \"title\": \"포트폴리오 분석\",\n" +
                "    \"isPositive\": true,\n" +
                "    \"analysisResult\": \"(결과 분석 내용)\",\n" +
                "    \"feedbackList\": [\"보완점1\", \"보완점2\"]\n" +
                "  }\n" +
                "}";
    }
}