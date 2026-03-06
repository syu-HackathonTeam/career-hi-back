package com.careerhi.api.domain.file.controller;

import com.careerhi.api.domain.file.dto.FileResponse;
import com.careerhi.api.domain.file.service.FileService;
import com.careerhi.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileResponse response = fileService.upload(file);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping
    public ApiResponse<Void> deleteFile(@RequestParam("fileName") String fileName) {
        fileService.delete(fileName);
        return ApiResponse.success("파일이 성공적으로 삭제되었습니다.");
    }
}