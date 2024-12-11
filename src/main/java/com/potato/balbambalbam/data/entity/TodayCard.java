package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "today_card")
@NoArgsConstructor
@Getter
public class TodayCard {
    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @Column(name = "text")
    private String text;

    @Column(name = "card_pronunciation")
    private String cardPronunciation;

    @Column(name = "card_summary")
    private String cardSummary;

    @Lob
    @Column(name = "child_male", columnDefinition = "MEDIUMBLOB", length = 16777215)
    private String childMale;

    @Lob
    @Column(name = "child_female", columnDefinition = "MEDIUMBLOB", length = 16777215)
    private String childFemale;

    @Lob
    @Column(name = "adult_male", columnDefinition = "MEDIUMBLOB", length = 16777215)
    private String adultMale;

    @Lob
    @Column(name = "adult_female", columnDefinition = "MEDIUMBLOB", length = 16777215)
    private String adultFemale;

    @Lob
    @Column(name = "elderly_male", columnDefinition = "MEDIUMBLOB", length = 16777215)
    private String elderlyMale;

    @Lob
    @Column(name = "elderly_female", columnDefinition = "MEDIUMBLOB", length = 16777215)
    private String elderlyFemale;

    public void saveCardVoice (String childMale, String childFemale, String adultMale, String adultFemale, String elderlyMale, String elderlyFemale) {
        this.childMale = childMale;
        this.childFemale = childFemale;
        this.adultMale = adultMale;
        this.adultFemale = adultFemale;
        this.elderlyMale = elderlyMale;
        this.elderlyFemale = elderlyFemale;
    }
}