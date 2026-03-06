package com.careerhi.api.domain.file.service;

import com.careerhi.api.domain.file.dto.FileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileResponse upload(MultipartFile file);
    void delete(String fileName); // [추가] 파일 삭제 메서드
}