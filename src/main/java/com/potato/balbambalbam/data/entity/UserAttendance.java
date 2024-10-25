package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity(name = "user_attendance")
@Getter
@Setter
public class UserAttendance {

    @Id
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;
    @Column(name = "is_present", nullable = false)
    private Boolean isPresent;

    public UserAttendance() {
    }
}
