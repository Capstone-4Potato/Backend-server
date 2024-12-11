package com.potato.balbambalbam.myReport.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "취약음소 테스트 목록 Response")
public class WeakSoundTestListDto {
    private Long id;
    private String text;
    private String pronunciation;
    private String engPronunciation;
    private String engTranslation;

    public WeakSoundTestListDto(Long id, String text, String pronunciation, String engPronunciation, String engTranslation) {
        this.id = id;
        this.text = text;
        this.pronunciation = pronunciation;
        this.engPronunciation = engPronunciation;
        this.engTranslation = engTranslation;
    }
}
