package com.potato.balbambalbam.user.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Notification 목록 조회")
public class NotificationDto {

    @Schema(description = "알림 ID", example = "1")
    private Long id;
    @Schema(description = "알림 제목", example = "App updated")
    private String title;
    @Schema(description = "알림 내용", example = "Bug fixes and new updates have been completed.")
    private String content;
    @Schema(description = "알림 읽음 여부", example = "false")
    private Boolean unread;
    @Schema(description = "알림 생성 시간", example = "2024-11-13T17:47:00")
    private LocalDateTime createdAt;
}
