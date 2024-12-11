package com.potato.balbambalbam.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "에러 발생 시 응답")
public class ExceptionDto {

    @Schema(description = "에러 코드")
    private int statusCode;
    @Schema(description = "에러 이름")
    private String exceptionName;
    @Schema(description = "에러 메세지")
    private String message;


}
