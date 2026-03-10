package com.careerhi.api.global.infra.sms;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Value("${coolsms.from-number}")
    private String fromNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        // SDK 초기화
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendVerificationCode(String toNumber, String authCode) {
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(toNumber);
        message.setText("[Career-HI] 인증번호는 [" + authCode + "] 입니다. 3분 내에 입력해 주세요.");

        try {
            this.messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) {
            // 실무에서는 커스텀 예외를 던지는 것이 좋습니다.
            throw new RuntimeException("SMS 발송에 실패했습니다: " + e.getMessage());
        }
    }
}