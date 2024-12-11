package com.potato.balbambalbam.card.cardFeedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AiFeedbackResponseDto {
    private String userText;
    private List<Integer> userMistakenIndexes;
    private Integer userAccuracy;
    private List<String> recommendedLastPronunciations;
    private List<String> recommendedPronunciations;

    private String userAudio;
    private List<AmplitudeData> userAmplitude;
    private String correctAudio;
    private List<AmplitudeData>  correctAmplitude;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class AmplitudeData {
        @JsonProperty("Time (s)")
        private double time;
        @JsonProperty("Amplitude")
        private double amplitude;
    }
}
