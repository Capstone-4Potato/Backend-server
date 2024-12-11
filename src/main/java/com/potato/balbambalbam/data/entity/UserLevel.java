package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "user_level")
@Getter
@Setter
public class UserLevel {

    @Id
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "level_id")
    private Long levelId;
    @Column(name = "user_experience")
    private Long userExperience;
    @Column(name = "category_id")
    private Long categoryId;

    public UserLevel() {
    }
}