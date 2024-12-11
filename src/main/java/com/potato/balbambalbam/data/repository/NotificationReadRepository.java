package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.NotificationRead;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long> {
    List<NotificationRead> findByUserId(Long userId);
    boolean existsByNotificationIdAndUserId(Long notificationId, Long userId);
}
