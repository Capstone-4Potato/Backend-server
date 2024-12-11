package com.potato.balbambalbam.card.cardFeedback.dto;

import com.potato.balbambalbam.card.cardInfo.dto.CardInfoResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Schema(name = "카드 피드백 Response")
public class UserFeedbackResponseDto {
    @Schema(example = "1")
    private Long cardId;

    @Schema(example = "[0, 1]")
    private List<Integer> mistakenIndexes;

    @Schema(example = "사용자가 발음한 소리")
    private String userText;

    @Schema(example = "100")
    private Integer userScore;

    private Map<String, CardInfoResponseDto> recommendCard;

    private CorrectAudio correctAudio;

    private UserAudio userAudio;

    @Getter
    @Setter
    @Schema(name = "사용자 amplitude")
    public static class UserAudio {

        @Schema(example = "base64 목소리")
        private String text;

        private List<AiFeedbackResponseDto.AmplitudeData> amplitude;

        public UserAudio(String text, List<AiFeedbackResponseDto.AmplitudeData> amplitude) {
            this.text = text;
            this.amplitude = amplitude;
        }
    }

    @Getter
    @Setter
    @Schema(name = "tts amplitude")
    public static class CorrectAudio {

        @Schema(example = "base64 목소리")
        private String text;
        private List<AiFeedbackResponseDto.AmplitudeData> amplitude;

        public CorrectAudio(String text, List<AiFeedbackResponseDto.AmplitudeData> amplitude) {
            this.text = text;
            this.amplitude = amplitude;
        }
    }
}
