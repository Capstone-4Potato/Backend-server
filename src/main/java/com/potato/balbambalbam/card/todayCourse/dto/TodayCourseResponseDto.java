package com.potato.balbambalbam.card.todayCourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Today Course Response")
public class TodayCourseResponseDto {
    @Schema(description = "학습 카드 아이디 리스트", example = "1, 2, 3, 4, 5, 6, 7, 8, 9, 10")
    private List<Long> cardIdList;

    public TodayCourseResponseDto(List<Long> cardIdList) {
        this.cardIdList = cardIdList;
    }
}