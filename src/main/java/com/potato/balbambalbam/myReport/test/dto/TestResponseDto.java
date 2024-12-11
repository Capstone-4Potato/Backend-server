package com.potato.balbambalbam.myReport.test.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TestResponseDto {
    private Map<String, Integer> userWeakPhoneme; //초성, 중성
    private Map<String, Integer> userWeakPhonemeLast; //종성
}