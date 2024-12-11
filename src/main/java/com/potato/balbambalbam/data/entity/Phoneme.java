package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;


@Entity(name = "phoneme")
@Getter
public class Phoneme {

    @Id
    private Long id;
    @Column(name = "type", nullable = false)
    private Long type;
    @Column(name = "text", nullable = false)
    private String text;

    public Phoneme() {
    }
}
