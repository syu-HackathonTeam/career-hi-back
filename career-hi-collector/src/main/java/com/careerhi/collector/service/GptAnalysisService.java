package com.careerhi.collector.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class GptAnalysisService {

    private final String API_KEY = "";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GptAnalysisService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public AnalysisResult analyze(String rawText) {
        try {
            String prompt = """
                너는 채용 공고 분석 AI야. 제공된 텍스트와 이미지(OCR) 내용을 바탕으로 JSON 데이터를 추출해.
                
                [중요 규칙]
                1. **requiredSkills(필수)**와 **preferredSkills(우대)**를 구분하는 것이 가장 중요해. 
                   - 본문에 '자격요건', '필수사항', 'Must have'에 적힌 기술은 반드시 requiredSkills에 넣어.
                   - '우대사항', 'Nice to have'에 적힌 기술은 preferredSkills에 넣어.
                   - 단순한 나열식 키워드(예: C++, Python)보다 **문장 속에 포함된 구체적인 기술(예: Kubernetes, Docker, Pytorch)**을 우선적으로 추출해.
                
                2. jobCategory는 다음 중 하나: '백엔드 개발', '프론트엔드 개발', '데이터 분석', '데이터 엔지니어', '보안 컨설팅', '개발 PM', '기타 개발'.
                
                3. 내용은 영어 대문자로 표준화해. (예: k8s -> KUBERNETES)
                
                [공고 데이터]
                %s
                
                [응답 예시]
                {
                  "companyName": "오픈AI",
                  "jobTitle": "백엔드 개발자",
                  "jobCategory": "백엔드 개발",
                  "requiredSkills": "JAVA, SPRING BOOT",
                  "preferredSkills": "AWS, KUBERNETES"
                }
                """.formatted(rawText.replace("\"", "'").replace("\n", " ").substring(0, Math.min(rawText.length(), 15000)));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-5-mini"); // ★ 최신 모델 지정
            requestBody.put("messages", new Object[]{
                    Map.of("role", "system", "content", "You are a helpful assistant. Respond in JSON."),
                    Map.of("role", "user", "content", prompt)
            });
            requestBody.put("response_format", Map.of("type", "json_object"));

            String response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            return objectMapper.readValue(content, AnalysisResult.class);

        } catch (Exception e) {
            System.out.println("❌ GPT 오류: " + e.getMessage());
            return new AnalysisResult("미수집", "개발자 채용", "기타 개발", "", "");
        }
    }

    public record AnalysisResult(
            String companyName,
            String jobTitle,
            String jobCategory,
            String requiredSkills,
            String preferredSkills
    ) {}
}