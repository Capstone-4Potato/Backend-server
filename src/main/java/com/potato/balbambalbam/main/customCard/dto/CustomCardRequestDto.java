package com.potato.balbambalbam.main.customCard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomCardRequestDto {
    @NotBlank(message = "문장을 작성해주세요")
    @Size(min = 1, max = 35, message = "1 ~ 35자 사이로 작성해야합니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣\s,.?!]*$", message = "한국어로 작성해야합니다.")
    String text;
}
