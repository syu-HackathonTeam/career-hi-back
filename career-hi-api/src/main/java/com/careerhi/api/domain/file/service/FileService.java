package com.careerhi.api.domain.file.service;

import com.careerhi.api.domain.file.dto.FileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileResponse upload(MultipartFile file);
}