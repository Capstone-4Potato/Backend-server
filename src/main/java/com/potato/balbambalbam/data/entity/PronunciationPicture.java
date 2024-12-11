package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;

@Entity(name = "pronunciation_picture")
@Getter
@ToString
public class PronunciationPicture {
    @Id
    private Long id;
    @Column(name = "phoneme_id", nullable = false)
    private Long phonemeId;
    @Column(name = "explanation")
    private String explanation;

}
