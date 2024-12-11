package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "weaksound_test_status")
@Getter
@Setter
public class WeakSoundTestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "test_status", nullable = false)
    private Boolean testStatus;

    public WeakSoundTestStatus(Long userId, boolean testStatus) {
        this.userId = userId;
        this.testStatus = testStatus;
    }

    public WeakSoundTestStatus() {

    }
}
