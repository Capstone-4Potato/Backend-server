package com.potato.balbambalbam.myReport.report.service;

import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CardScoreRepository;
import com.potato.balbambalbam.data.repository.UserAttendanceRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.myReport.report.dto.ReportInfoDto;
import com.potato.balbambalbam.myReport.weaksound.service.PhonemeService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportInfoService {
    private final UserRepository userRepository;
    private final UserAttendanceRepository userAttendanceRepository;
    private final CardScoreRepository cardScoreRepository;
    private final PhonemeService phonemeService;

    @Transactional(readOnly = true)
    public ReportInfoDto getMyReportInfo(Long userId) {
        ReportInfoDto reportInfoDto = new ReportInfoDto();

        // 1. 사용자 기본 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        reportInfoDto.setNickname(user.getName());

        // 2. 출석 체크한 총 일수
        Long studyDays = userAttendanceRepository.countByUserIdAndIsPresent(userId, true);
        reportInfoDto.setStudyDays(studyDays);

        // 3. 전체 학습 통계
        // 총 학습 카드 수
        List<CardScore> allCardScores = cardScoreRepository.findByUserId(userId);
        reportInfoDto.setTotalLearned((long) allCardScores.size());

        // 전체 학습 정확도 - 소수점 첫째자리까지 반올림
        Double accuracy = cardScoreRepository.getAverageScoreByUserId(userId);
        double roundedAccuracy = accuracy != null ? Math.round(accuracy * 10) / 10.0 : 0.0;
        reportInfoDto.setAccuracy(roundedAccuracy);

        // 4. 이번 주 요일별 학습 카드 수 계산
        LocalDate today = LocalDate.now();

        // 이전 주 일요일 찾기 (오늘이 일요일이면 오늘이 시작일)
        LocalDate startOfWeek = today.getDayOfWeek() == DayOfWeek.SUNDAY ?
                today : today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));

        long daysFromSunday = ChronoUnit.DAYS.between(startOfWeek, today) + 1;

        // 요일별 카드 수를 저장할 Map 초기화
        Map<DayOfWeek, Long> cardsByDay = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            cardsByDay.put(day, 0L);
        }

        List<CardScore> weeklyCardScores = cardScoreRepository.findByUserIdAndTimeStampBetween(
                userId,
                startOfWeek.atStartOfDay(),
                today.atTime(23, 59, 59)
        );

        weeklyCardScores.forEach(score -> {
            DayOfWeek dayOfWeek = score.getTimeStamp().getDayOfWeek();
            cardsByDay.merge(dayOfWeek, 1L, Long::sum);
        });

        // 각 요일별 카드 수 설정
        reportInfoDto.setSundayCards(cardsByDay.get(DayOfWeek.SUNDAY));
        reportInfoDto.setMondayCards(cardsByDay.get(DayOfWeek.MONDAY));
        reportInfoDto.setTuesdayCards(cardsByDay.get(DayOfWeek.TUESDAY));
        reportInfoDto.setWednesdayCards(cardsByDay.get(DayOfWeek.WEDNESDAY));
        reportInfoDto.setThursdayCards(cardsByDay.get(DayOfWeek.THURSDAY));
        reportInfoDto.setFridayCards(cardsByDay.get(DayOfWeek.FRIDAY));
        reportInfoDto.setSaturdayCards(cardsByDay.get(DayOfWeek.SATURDAY));


        // 5. 이번 주 평균 카드 수 계산
        long totalWeeklyCards = weeklyCardScores.size();
        reportInfoDto.setWeeklyAverageCards(daysFromSunday > 0 ? totalWeeklyCards / daysFromSunday : 0);

        // 6. 취약 음소 목록 조회
        reportInfoDto.setWeakPhonemes(phonemeService.getWeakPhonemes(userId));

        return reportInfoDto;
    }
}