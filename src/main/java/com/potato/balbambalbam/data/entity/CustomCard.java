package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "custom_card")
@Getter @Setter
@NoArgsConstructor
public class CustomCard {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text")
    private String text;
    @Column(name = "eng_pronunciation")
    private String engPronunciation;
    @Column(name = "eng_translation")
    private String engTranslation;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "highest_score")
    private Integer highestScore;
    @Column(name = "bookmark")
    private Boolean isBookmarked;

}
