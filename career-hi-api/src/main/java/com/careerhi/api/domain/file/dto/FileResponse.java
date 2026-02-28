package com.careerhi.api.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileResponse {
    private String FileName;
    private String FileUrl;
}