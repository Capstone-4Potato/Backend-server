package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;

@Entity(name = "category")
@Getter
@ToString
public class Category {

    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "parent_id")
    private Long parentId;

    public Category() {

    }
}
