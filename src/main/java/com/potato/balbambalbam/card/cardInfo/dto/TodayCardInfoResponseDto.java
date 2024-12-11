package com.potato.balbambalbam.card.cardInfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "오늘의 카드 정보 Response")
public class TodayCardInfoResponseDto {

    private Long id;

    @Schema(description = "card 내용", example = "남사친")
    private String text;

    @Schema(description = "card 로마자 발음", example = "namsachin")
    private String cardPronunciation;

    @Schema(description = "card 설명", example = "Shortened term for a male friend who is 'just a friend'")
    private String cardSummary;

    @Schema(description = "card TTS (in base64)", example = "너무 길어서 생략")
    private String correctAudio;
}
