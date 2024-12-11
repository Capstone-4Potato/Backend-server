package com.potato.balbambalbam.user.attendance.controller;

import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.user.attendance.dto.AttendanceResponseDto;
import com.potato.balbambalbam.user.attendance.service.AttendanceService;
import com.potato.balbambalbam.user.join.service.JoinService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Attendance API", description = "전체 출석 정보를 보여준다.")
public class AttendanceController {
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final AttendanceService attendanceService;

    private Long extractUserIdFromToken(String access) { // access 토큰으로부터 userId 추출하는 함수
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @Operation(summary = "사용자 출석 날짜 조회", description = "사용자의 월별 출석 날짜를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = AttendanceResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/home/attendance")
    public ResponseEntity<AttendanceResponseDto> getAttendanceDates(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        Map<String, List<Integer>> attendanceDates = attendanceService.getAttendanceDates(userId);
        return ResponseEntity.ok(new AttendanceResponseDto(attendanceDates));
    }
}
