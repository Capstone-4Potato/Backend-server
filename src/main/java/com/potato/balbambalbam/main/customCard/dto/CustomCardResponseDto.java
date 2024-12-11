package com.potato.balbambalbam.main.customCard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomCardResponseDto {
    private Long id;
    private String text;
    private String engPronunciation;
    private String engTranslation;
}
