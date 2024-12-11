package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity(name = "card_voice")
@ToString
@NoArgsConstructor
@Builder
public class CardVoice {
    @Id
    @Column(name = "card_id")
    private Long id;
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

    @Builder
    public CardVoice(Long id, String childMale, String childFemale, String adultMale, String adultFemale, String elderlyMale, String elderlyFemale) {
        this.id = id;
        this.childMale = childMale;
        this.childFemale = childFemale;
        this.adultMale = adultMale;
        this.adultFemale = adultFemale;
        this.elderlyMale = elderlyMale;
        this.elderlyFemale = elderlyFemale;
    }
}