package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "notification_reads")
@NoArgsConstructor
@Getter
@Setter
@IdClass(NotificationId.class)
public class NotificationRead {

    @Id
    @Column(name = "notification_id")
    private Long notificationId;

    @Id
    @Column(name = "user_id")
    private Long userId;
}
