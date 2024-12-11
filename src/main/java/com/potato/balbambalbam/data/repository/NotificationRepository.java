package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM notifications n WHERE n.isActive = true ORDER BY n.id DESC")
    List<Notification> findByIsActiveTrueOrderByIdDesc();
}
