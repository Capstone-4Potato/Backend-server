package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity(name = "card_score")
@NoArgsConstructor
@Getter
@ToString
public class CardScore {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "highest_score", nullable = false)
    private int highestScore;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Setter
    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;

    public CardScore(int highestScore, Long userId, Long cardId, LocalDateTime timeStamp) {
        this.highestScore = highestScore;
        this.userId = userId;
        this.cardId = cardId;
        this.timeStamp = timeStamp;
    }
}