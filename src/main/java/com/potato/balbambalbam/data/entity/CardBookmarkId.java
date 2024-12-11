package com.potato.balbambalbam.data.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CardBookmarkId implements Serializable {
    private Long userId;
    private Long cardId;
}