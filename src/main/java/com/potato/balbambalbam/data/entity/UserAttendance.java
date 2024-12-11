package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity(name = "user_attendance")
@Getter
@Setter
@IdClass(UserAttendanceId.class)
public class UserAttendance {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    @Column(name = "is_present")
    private Boolean isPresent;

    public UserAttendance() {
    }
}
