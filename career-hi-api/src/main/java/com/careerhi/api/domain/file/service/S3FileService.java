package com.careerhi.api.domain.file.service;

import com.careerhi.api.domain.file.dto.FileResponse;
import com.careerhi.common.exception.CustomException;
import com.careerhi.common.exception.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Primary // ★ 중요: 기존 LocalFileService 대신 이 서비스를 우선적으로 사용하게 합니다.
@RequiredArgsConstructor
public class S3FileService implements FileService {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public FileResponse upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE); // 파일 없음 예외
        }

        // 1. 파일명 중복 방지를 위한 UUID 생성 (1.5TB 관리 짬에서 나오는 바이브!)
        String originalFileName = file.getOriginalFilename();
        String savedFileName = UUID.randomUUID() + "_" + originalFileName;

        try {
            // 2. S3에 파일 업로드
            var resource = s3Template.upload(bucket, savedFileName, file.getInputStream(),
                    ObjectMetadata.builder().contentType(file.getContentType()).build());

            // 3. 업로드된 파일의 퍼블릭 URL 획득
            String fileUrl = resource.getURL().toString();

            // 명세서 2-0 구조에 맞춰 PascalCase로 응답
            return new FileResponse(originalFileName, fileUrl);

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }
}