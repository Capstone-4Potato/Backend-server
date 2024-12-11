package com.potato.balbambalbam.myReport.report.controller;

import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.myReport.report.dto.ReportInfoDto;
import com.potato.balbambalbam.myReport.report.service.ReportInfoService;
import com.potato.balbambalbam.user.join.service.JoinService;
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
@Tag(name = "My Report API", description = "My Report에 필요한 ...을 반환한다.")
public class ReportController {
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final ReportInfoService reportInfoService;

    private Long extractUserIdFromToken(String access) { // access 토큰으로부터 userId 추출하는 함수
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @Operation(summary = "My report 화면 정보 조회", description = "My report 화면에 필요한 모든 정보를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "My report 화면 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportInfoDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/report")
    public ResponseEntity<ReportInfoDto> getHomeInfo(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        ReportInfoDto response = reportInfoService.getMyReportInfo(userId);
        return ResponseEntity.ok(response);
    }
}
