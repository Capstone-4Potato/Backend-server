package com.potato.balbambalbam.user.home.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "홈 기본 정보 Response")
public class HomeInfoDto {

    @Schema(description = "사용자 레벨", example = "3")
    private Long userLevel;
    @Schema(description = "레벨 총 경험치", example = "30")
    private Long levelExperience;
    @Schema(description = "사용자 경험치", example = "17")
    private Long userExperience;
    @Schema(description = "주간 출석 상황", example = "TFFTTFF")
    private String weeklyAttendance;
    @Schema(description = "오늘의 추천 단어 Id", example = "1")
    private Long dailyWordId;
    @Schema(description = "오늘의 추천 단어", example = "든든해")
    private String dailyWord;
    @Schema(description = "오늘의 추천 단어 발음법", example = "deun-deun-hae")
    private String dailyWordPronunciation;
    @Schema(description = "Saved Cards 수", example = "1")
    private Long savedCardNumber;
    @Schema(description = "Missed Cards 수", example = "2")
    private Long missedCardNumber;
    @Schema(description = "Custom Cards 수", example = "3")
    private Long customCardNumber;
    @Schema(description = "읽지 않은 알림 존재 여부", example = "true")
    private boolean hasUnreadNotifications;
}
