package com.potato.balbambalbam.home.learningCourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "카드 리스트 Response")
public class CardListResponseDto<T> {
    private T cardList;
    private int count;

    public CardListResponseDto(T cardList, int count) {
        this.cardList = cardList;
        this.count = count;
    }
}
