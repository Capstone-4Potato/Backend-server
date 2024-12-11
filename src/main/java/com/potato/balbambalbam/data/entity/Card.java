package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity(name = "card")
@NoArgsConstructor
@Getter
@ToString
public class Card {
    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "text", nullable = false)
    private String text;

    @Setter
    @Column(name = "card_pronunciation")
    private String cardPronunciation;

    @Setter
    @Column(name = "card_phonemes")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Long> phonemesMap;

    @Setter
    @Column(name = "card_translation")
    private String cardTranslation;
}