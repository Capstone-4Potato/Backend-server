package com.potato.balbambalbam.main.cardFeedback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AiFeedbackResponseDto {
    private String userText;
    private List<Integer> userMistakenIndexes;
    private Integer userAccuracy;
    private Double correctAudioDuration;
    private String correctWaveform;
    private Double userAudioDuration;
    private String userWaveform;
    private List<Character> recommendedLastPronunciations;
    private List<Character> recommendedPronunciations;

}
