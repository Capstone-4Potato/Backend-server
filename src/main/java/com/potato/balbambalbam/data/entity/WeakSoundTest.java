package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name="weaksound_test")
public class WeakSoundTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String pronunciation;

    @Column(name = "eng_pronunciation", nullable = false)
    private String engPronunciation;

    @Column(name = "eng_translation", nullable = false)
    private String engTranslation;
}
