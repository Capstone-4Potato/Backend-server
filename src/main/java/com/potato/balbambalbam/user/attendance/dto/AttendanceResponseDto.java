package com.potato.balbambalbam.user.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
@Schema(description = "출석 날짜 응답 DTO")
public class AttendanceResponseDto {
    @Schema(description = "월별 출석일 목록 (key: YYYY-MM, value: 출석한 일자 리스트)")
    private Map<String, List<Integer>> attendanceByMonth;

    public AttendanceResponseDto(Map<String, List<Integer>> attendanceByMonth) {
        this.attendanceByMonth = attendanceByMonth;
    }
}
