package com.potato.balbambalbam.user.join.jwt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserDto {
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private String name;
}
