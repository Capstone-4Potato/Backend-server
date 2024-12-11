package com.potato.balbambalbam.user.notification.controller;

import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.user.join.service.JoinService;
import com.potato.balbambalbam.user.notification.dto.NotificationDto;
import com.potato.balbambalbam.user.notification.service.NotificationService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Notification API", description = "알림 관련 정보 ")
public class NotificationController {
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final NotificationService notificationService;

    private Long extractUserIdFromToken(String access) { // access 토큰으로부터 userId 추출하는 함수
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @Operation(summary = "알림 목록 조회", description = "알림을 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 반환 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/notification")
    public ResponseEntity<List<NotificationDto>> getNotification(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        List<NotificationDto> notifications = notificationService.getActiveNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 알림 읽음 처리 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/notification/{notificationId}")
    public ResponseEntity<Void> readNotification(@PathVariable Long notificationId, @RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }
}
