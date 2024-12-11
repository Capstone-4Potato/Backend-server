package com.potato.balbambalbam.myReport.report.dto;

import com.potato.balbambalbam.myReport.weaksound.dto.UserWeakSoundResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "My Report 화면 Response")

public class ReportInfoDto {
    @Schema(description = "사용자 닉네임")
    private String nickname;

    @Schema(description = "출석체크한 총 일수")
    private Long studyDays;

    @Schema(description = "학습한 총 카드 수")
    private Long totalLearned;

    @Schema(description = "전체 학습 정확도 (%)")
    private Double accuracy;

    @Schema(description = "이번 주 평균 학습 카드 수")
    private Long weeklyAverageCards;

    @Schema(description = "일요일 학습 카드 수")
    private Long sundayCards;

    @Schema(description = "월요일 학습 카드 수")
    private Long mondayCards;

    @Schema(description = "화요일 학습 카드 수")
    private Long tuesdayCards;

    @Schema(description = "수요일 학습 카드 수")
    private Long wednesdayCards;

    @Schema(description = "목요일 학습 카드 수")
    private Long thursdayCards;

    @Schema(description = "금요일 학습 카드 수")
    private Long fridayCards;

    @Schema(description = "토요일 학습 카드 수")
    private Long saturdayCards;

    @Schema(description = "취약 음소 목록")
    private List<UserWeakSoundResponseDto> weakPhonemes;

}
