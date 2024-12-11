package com.potato.balbambalbam.home.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Bookmark Card 리스트 Response")
public class BookmarkCardResponseDto {
    private List<CardDto> cardList;

    public BookmarkCardResponseDto(List<CardDto> cardList) {
        this.cardList = cardList;
    }
}
