package com.potato.balbambalbam.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "복습카드 리스트 Response")
public class ReviewListResponseDto<T> {
    private T cardList;
    private int count;

    public ReviewListResponseDto(T cardList, int count) {
        this.cardList = cardList;
        this.count = count;
    }

}
