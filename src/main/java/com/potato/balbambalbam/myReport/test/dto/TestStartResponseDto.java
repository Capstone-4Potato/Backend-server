package com.potato.balbambalbam.myReport.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "취약음소 테스트 시작 상태 Response")
public class TestStartResponseDto {
    @Schema(description = "진행 중이던 테스트 존재 여부")
    private boolean hasUnfinishedTest;
}
