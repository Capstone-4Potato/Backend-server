package com.potato.balbambalbam.myReport.weaksound.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PhonemeResponseDto {
    private Long id;
    private String type;
    private String text;
    private boolean isWeak;
}
