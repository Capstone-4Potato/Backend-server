package com.potato.balbambalbam.user.home.controller;

import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.user.home.dto.HomeInfoDto;
import com.potato.balbambalbam.user.home.service.HomeInfoService;
import com.potato.balbambalbam.user.join.service.JoinService;
import com.potato.balbambalbam.user.notification.service.NotificationService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Home API", description = "홈 화면에 필요한 사용자 레벨, 레벨 경험치, 사용자 경험치, 주간 출석 상황, 오늘의 추천 단어를 반환한다. ")
public class HomeController {

    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final HomeInfoService homeInfoService;
    private final NotificationService notificationService;

    private Long extractUserIdFromToken(String access) { // access 토큰으로부터 userId 추출하는 함수
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @Operation(summary = "홈 화면 정보 조회", description = "사용자의 홈 화면에 필요한 모든 정보를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "홈 화면 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HomeInfoDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/home")
    public ResponseEntity<HomeInfoDto> getHomeInfo(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        HomeInfoDto response = homeInfoService.getHomeInfo(userId);
        return ResponseEntity.ok(response);
    }
}
