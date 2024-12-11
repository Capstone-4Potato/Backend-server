package com.potato.balbambalbam.card.tts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiAllTtsResponseDto {
    public String child_0;
    public String child_1;
    public String adult_0;
    public String adult_1;
    public String elderly_0;
    public String elderly_1;

    @Builder
    public AiAllTtsResponseDto(String child_0, String child_1, String adult_0, String adult_1, String elderly_0, String elderly_1) {
        this.child_0 = child_0;
        this.child_1 = child_1;
        this.adult_0 = adult_0;
        this.adult_1 = adult_1;
        this.elderly_0 = elderly_0;
        this.elderly_1 = elderly_1;
    }
}
