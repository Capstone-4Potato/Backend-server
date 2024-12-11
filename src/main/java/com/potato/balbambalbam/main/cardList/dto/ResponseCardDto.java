package com.potato.balbambalbam.main.cardList.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter @Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "카드리스트 각 카드 정보 Response")
public class ResponseCardDto {

    private Long id;
    private String text;
    private String engTranslation;
    private String engPronunciation;
    private boolean isBookmark;
    private boolean isWeakCard;
    private int cardScore;
    private String pictureUrl;
    private String explanation;

}
