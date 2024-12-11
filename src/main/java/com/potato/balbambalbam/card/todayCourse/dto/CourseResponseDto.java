package com.potato.balbambalbam.card.todayCourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Today Course Response")
public class CourseResponseDto<T> {
    private T cardList;
    private int count;

    public CourseResponseDto(T cardList, int count) {
        this.cardList = cardList;
        this.count = count;
    }
}

