package com.potato.balbambalbam.user.join.jwt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinDto {

    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private String name;
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private String socialId;
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private Integer age;
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private Byte gender;

}