package com.potato.balbambalbam.card.cardInfo.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AiTtsRequestDto {
    public Integer age;
    public Byte gender;  //0 : 여자 , 1 : 남자

    public String text;

}
