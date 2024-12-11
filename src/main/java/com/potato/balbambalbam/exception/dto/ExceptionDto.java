package com.potato.balbambalbam.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "에러 발생 시 응답")
public class ExceptionDto {

    @Schema(description = "HTTP 상태 코드")
    private int statusCode;

    @Schema(description = "예외 이름")
    private String exceptionName;

    @Schema(description = "예외 메시지")
    private String message;

    @Schema(description = "예외 레벨")
    private String exceptionLevel;


    // 기존 생성자 유지
    public ExceptionDto(int statusCode, String exceptionName, String message) {
        this.statusCode = statusCode;
        this.exceptionName = exceptionName;
        this.message = message;
        this.exceptionLevel = determineExceptionLevel(statusCode);
    }

    private String determineExceptionLevel(int statusCode) {
        if (statusCode >= 500) {
            return "ERROR";
        } else if (statusCode >= 400) {
            return "WARN";
        } else {
            return "INFO";
        }
    }
}