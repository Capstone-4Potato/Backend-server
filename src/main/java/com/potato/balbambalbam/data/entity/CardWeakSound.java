package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "card_weaksound")
@Getter
@NoArgsConstructor
@IdClass(CardWeakSoundId.class)
public class CardWeakSound {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Id
    @Column(name = "card_id")
    private Long cardId;

    public CardWeakSound(Long userId, Long cardId) {
        this.userId = userId;
        this.cardId = cardId;
    }
}