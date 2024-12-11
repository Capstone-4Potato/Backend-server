package com.potato.balbambalbam.user.join.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "새로운 사용자 정보 Response")
public class JoinResponseDto {

    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private String name;
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private String socialId;
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private Integer age;
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private Byte gender;

}