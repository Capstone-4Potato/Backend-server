package com.potato.balbambalbam.home.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Schema(description = "missedCard 리스트 Response")
public class MissedCardResponseDto {
    private Map<String, List<CardDto>> cardList;

    public MissedCardResponseDto(Map<String, List<CardDto>> cardList) {
        this.cardList = cardList;
    }
}
