package com.careerhi.api.domain.file.controller;

import com.careerhi.api.domain.file.dto.FileResponse;
import com.careerhi.api.domain.file.service.FileService;
import com.careerhi.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "2. File", description = "이미지 및 문서 파일 관리 API")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // Create: 파일 추가
    @Operation(summary = "파일 업로드", description = "Multipart 형식의 파일을 스토리지에 저장합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileResponse response = fileService.upload(file);
        return ApiResponse.success("파일 업로드가 완료되었습니다.", response);
    }
    // Delete: 파일 삭제
    @Operation(summary = "파일 삭제", description = "저장된 파일명을 기준으로 파일을 영구 삭제합니다.")
    @DeleteMapping("/{fileName}")
    public ApiResponse<Void> deleteFile(@PathVariable String fileName) {
        fileService.delete(fileName);
        return ApiResponse.success("파일이 성공적으로 삭제되었습니다.");
    }
    // Update는 프론트에서 기존 파일을 Delete 호출 후 다시 Upload 하거나,
    // 동일한 파일명으로 Upload를 재호출하면 S3에서 자동 덮어쓰기 됨. 참고!!!!!!!!!!!
}