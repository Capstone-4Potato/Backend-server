package com.potato.balbambalbam.home.customCard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomCardResponseDto {
    @Schema(example = "100")
    private Long id;

    @Schema(example = "안녕하세요")
    private String text;

    @Schema(example = "annyeonghasio")
    private String engPronunciation;

    @Schema(example = "hello")
    private String engTranslation;

    @Schema(example = "2024-11-18T10:04:33.932819")
    private LocalDateTime createdAt;
}
