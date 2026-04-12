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

    // Create: 파일 추가
    @PostMapping("/upload")
    public ApiResponse<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileResponse response = fileService.upload(file);
        return ApiResponse.success("파일 업로드가 완료되었습니다.", response);
    }
    // Delete: 파일 삭제
    @DeleteMapping("/{fileName}")
    public ApiResponse<Void> deleteFile(@PathVariable String fileName) {
        fileService.delete(fileName);
        return ApiResponse.success("파일이 성공적으로 삭제되었습니다.");
    }
    // Update는 프론트에서 기존 파일을 Delete 호출 후 다시 Upload 하거나,
    // 동일한 파일명으로 Upload를 재호출하면 S3에서 자동 덮어쓰기 됨. 참고!!!!!!!!!!!
}