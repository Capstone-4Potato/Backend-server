package com.potato.balbambalbam.card.cardInfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Schema(name = "Card 정보 Response")
public class CardInfoResponseDto {

    @Schema(description = "card Id", example = "1L")
    private Long id;

    @Schema(description = "card text", example = "아")
    private String text;

    @Schema(description = "card tts (in base64)", example = "너무 길어서 생략")
    private String correctAudio;

    @Schema(description = "card 한영 풀이", example = "a")
    private String cardTranslation;

    @Schema(description = "card 로마자 발음", example = "a")
    private String cardPronunciation;

    @Schema(description = "card 북마크 여부", example = "false")
    private boolean isBookmark;

    @Schema(description = "card 취약음 여부", example = "true")
    private boolean isWeakCard;

    @Setter
    @Schema(description = "card 음절인 경우 사진 제공", example = "/images/1")
    private String pictureUrl;

    @Setter
    @Schema(description = "card 음절인 경우 설명 제공", example = "Open your mouth wide, put your chin and tongue downward and vibrate your vocal cord.")
    private String explanation;

    @Builder
    public CardInfoResponseDto(Long id, String text, String correctAudio, String cardTranslation, String cardPronunciation, boolean isBookmark, boolean isWeakCard) {
        this.id = id;
        this.text = text;
        this.correctAudio = correctAudio;
        this.cardTranslation = cardTranslation;
        this.cardPronunciation = cardPronunciation;
        this.isBookmark = isBookmark;
        this.isWeakCard = isWeakCard;
    }
}
