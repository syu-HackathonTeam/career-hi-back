package com.careerhi.api.domain.file.controller;

import com.careerhi.api.domain.file.dto.FileResponse;
import com.careerhi.api.domain.file.service.FileService;
import com.careerhi.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ApiResponse<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileResponse response = fileService.upload(file);
        return ApiResponse.success("파일 업로드가 완료되었습니다.", response);
    }
}