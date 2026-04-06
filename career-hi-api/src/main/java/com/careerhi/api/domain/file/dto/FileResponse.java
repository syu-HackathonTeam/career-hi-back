package com.careerhi.api.domain.file.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileResponse {
    @JsonProperty("FileName") // JSON으로 나갈 때 이름을 고정
    private String fileName;

    @JsonProperty("FileUrl")
    private String fileUrl;
}