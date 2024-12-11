package com.potato.balbambalbam.user.notification.service;

import com.potato.balbambalbam.data.entity.Notification;
import com.potato.balbambalbam.data.entity.NotificationRead;
import com.potato.balbambalbam.data.repository.NotificationReadRepository;
import com.potato.balbambalbam.data.repository.NotificationRepository;
import com.potato.balbambalbam.user.notification.dto.NotificationDto;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationReadRepository notificationReadRepository;

    public List<NotificationDto> getActiveNotifications(Long userId) {
        List<Notification> activeNotifications = notificationRepository.findByIsActiveTrueOrderByIdDesc();

        Set<Long> readNotificationIds = notificationReadRepository.findByUserId(userId)
                .stream()
                .map(NotificationRead::getNotificationId)
                .collect(Collectors.toSet());

        return activeNotifications.stream()
                .map(notification -> {
                    NotificationDto dto = new NotificationDto();
                    dto.setId(notification.getId());
                    dto.setTitle(notification.getTitle());
                    dto.setContent(notification.getContent());
                    dto.setUnread(!readNotificationIds.contains(notification.getId()));
                    dto.setCreatedAt(notification.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        if (!notificationReadRepository.existsByNotificationIdAndUserId(notificationId, userId)) {
            NotificationRead notificationRead = new NotificationRead();
            notificationRead.setNotificationId(notificationId);
            notificationRead.setUserId(userId);
            notificationReadRepository.save(notificationRead);
        }
    }

    public boolean hasUnreadNotifications(Long userId) {
        List<Notification> activeNotifications = notificationRepository.findByIsActiveTrueOrderByIdDesc();
        Set<Long> allActiveNotificationIds = activeNotifications.stream()
                .map(Notification::getId)
                .collect(Collectors.toSet());

        Set<Long> readNotificationIds = notificationReadRepository.findByUserId(userId)
                .stream()
                .map(NotificationRead::getNotificationId)
                .collect(Collectors.toSet());

        return allActiveNotificationIds.stream()
                .anyMatch(id -> !readNotificationIds.contains(id));
    }
}