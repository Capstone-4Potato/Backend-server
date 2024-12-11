package com.potato.balbambalbam.home.customCard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Custom card 리스트 각 카드 정보 Response")
public class CustomCardDto {
    @Schema(example = "100")
    private Long id;

    @Schema(example = "안녕하세요")
    private String text;

    @Schema(example = "hello")
    private String engTranslation;

    @Schema(example = "annyeonghasio")
    private String engPronunciation;

    @Schema(example = "false")
    private boolean isBookmark;

    @Schema(example = "false")
    private boolean isWeakCard;

    @Schema(example = "90")
    private int cardScore;

    @Schema(example = "2024-11-18T10:04:33.932819")
    private LocalDateTime createdAt;
}