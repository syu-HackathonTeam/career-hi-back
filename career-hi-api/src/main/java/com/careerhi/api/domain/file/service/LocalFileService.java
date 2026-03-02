package com.careerhi.api.domain.file.service;

import com.careerhi.api.domain.file.dto.FileResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileService implements FileService {

    // 파일을 저장할 로컬 경로 (노트북 어딘가의 적절한 곳으로 수정해야함)
    private final String uploadDir = Paths.get(System.getProperty("user.home"), "careerhi-uploads").toString();

    @Override
    public FileResponse upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 저장 폴더가 없으면 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        // 파일명 중복 방지를 위한 UUID 생성
        String originalFileName = file.getOriginalFilename();
        String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        String filePath = Paths.get(uploadDir, savedFileName).toString();

        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }

        // 실제 서비스에서는 도메인 주소가 포함된 URL을 반환해야 함.
        // 여기서는 예시 주소를 반환합니다.
        String fileUrl = "http://localhost:8080/uploads/" + savedFileName;

        return new FileResponse(originalFileName, fileUrl);
    }
}