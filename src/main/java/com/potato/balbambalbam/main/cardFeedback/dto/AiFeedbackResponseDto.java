package com.potato.balbambalbam.main.cardFeedback.dto;

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
    private Double correctAudioDuration;
    private String correctWaveform;
    private Double userAudioDuration;
    private String userWaveform;
    private List<String> recommendedLastPronunciations;
    private List<String> recommendedPronunciations;



}
