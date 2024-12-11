package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "category")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Category {
    @Id
    @Column(name = "category_id")
    private Long category_id;
    @Column(name = "level", nullable = false)
    private String level;

}