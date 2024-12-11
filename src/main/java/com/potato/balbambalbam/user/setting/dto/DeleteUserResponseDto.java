package com.potato.balbambalbam.user.setting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "삭제할 사용자 ID Response")
public class DeleteUserResponseDto {
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private String name;
}
