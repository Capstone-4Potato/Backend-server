package com.potato.balbambalbam.learningInfo.weaksound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "사용자 취약음소 Top4 Response")
public class UserWeakSoundResponseDto {
    private int rank;
    private String phonemeText;

    public UserWeakSoundResponseDto(int rank, String phonemeText) {
        this.rank = rank;
        this.phonemeText = phonemeText;
    }
}
