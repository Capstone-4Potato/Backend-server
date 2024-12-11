package com.potato.balbambalbam.user.home.service;

import com.potato.balbambalbam.data.entity.Level;
import com.potato.balbambalbam.data.entity.TodayCard;
import com.potato.balbambalbam.data.entity.UserAttendance;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.CardBookmarkRepository;
import com.potato.balbambalbam.data.repository.CardScoreRepository;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.data.repository.LevelRepository;
import com.potato.balbambalbam.data.repository.TodayCardRepository;
import com.potato.balbambalbam.data.repository.UserAttendanceRepository;
import com.potato.balbambalbam.data.repository.UserLevelRepository;
import com.potato.balbambalbam.user.home.dto.HomeInfoDto;
import com.potato.balbambalbam.user.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeInfoService {
    private final UserLevelRepository userLevelRepository;
    private final LevelRepository levelRepository;
    private final UserAttendanceRepository userAttendanceRepository;
    private final TodayCardRepository todayCardRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final CardScoreRepository cardScoreRepository;
    private final CustomCardRepository customCardRepository;
    private final NotificationService notificationService;

    @Transactional
    public HomeInfoDto getHomeInfo(Long userId) {
        checkTodayAttendance(userId);
        HomeInfoDto homeInfoDto = new HomeInfoDto();

        setUserLevelInfo(userId, homeInfoDto);
        setUserAttendanceInfo(userId, homeInfoDto);
        setDailyWordInfo(homeInfoDto);
        setUserNumInfo(userId, homeInfoDto);
        homeInfoDto.setHasUnreadNotifications(notificationService.hasUnreadNotifications(userId));

        return homeInfoDto;
    }

    private void checkTodayAttendance(Long userId) {
        LocalDate today = LocalDate.now();
        UserAttendance todayAttendance = userAttendanceRepository
                .findByUserIdAndAttendanceDate(userId, today);

        if (todayAttendance == null) {
            UserAttendance newAttendance = new UserAttendance();
            newAttendance.setUserId(userId);
            newAttendance.setAttendanceDate(today);
            newAttendance.setIsPresent(true);
            userAttendanceRepository.save(newAttendance);
        }
    }

    private void setUserLevelInfo(Long userId, HomeInfoDto homeInfoDto) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found for user: " + userId));

        Level level = levelRepository.findByLevelId(userLevel.getLevelId())
                .orElseThrow(() -> new RuntimeException("Level not found for levelId: " + userLevel.getLevelId()));

        homeInfoDto.setUserLevel(level.getLevel());
        homeInfoDto.setLevelExperience(level.getLevelExperience());
        homeInfoDto.setUserExperience(userLevel.getUserExperience());
    }

    private void setUserAttendanceInfo(Long userId, HomeInfoDto homeInfoDto) {
        LocalDate now = LocalDate.now();
        LocalDate sunday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate saturday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        List<UserAttendance> attendances = userAttendanceRepository.findWeeklyAttendance(userId, sunday, saturday);

        char[] weekAttendance = new char[7]; // 일~토까지 7일
        Arrays.fill(weekAttendance, 'F');

        for (UserAttendance attendance : attendances) {
            if (attendance.getIsPresent()) {
                int dayOfWeek = attendance.getAttendanceDate().getDayOfWeek().getValue();
                if (dayOfWeek == 7) { // 일요일
                    weekAttendance[0] = 'T';
                } else { // 월~토
                    weekAttendance[dayOfWeek] = 'T';
                }
            }
        }
        homeInfoDto.setWeeklyAttendance(new String(weekAttendance));
    }

    private void setDailyWordInfo(HomeInfoDto homeInfoDto) {
        // 기준일 설정 (2024-10-26)
        LocalDate baseDate = LocalDate.of(2024, 10, 26);
        LocalDate today = LocalDate.now();
        long daysSinceBase = ChronoUnit.DAYS.between(baseDate, today);
        final int TOTAL_CARDS = 93;

        // 카드 ID 계산: (경과일 % 전체카드수) + 1
        long todayCardId = (daysSinceBase % TOTAL_CARDS) + 1;

        TodayCard todayCard = todayCardRepository.findById(todayCardId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Card not found for date %s (ID: %d)", today, todayCardId)));

        homeInfoDto.setDailyWordId(todayCard.getCardId());
        homeInfoDto.setDailyWord(todayCard.getText());
        homeInfoDto.setDailyWordPronunciation(todayCard.getCardPronunciation());
    }

    private void setUserNumInfo(Long userId, HomeInfoDto homeInfoDto) {
        Long savedCards = cardBookmarkRepository.countByUserId(userId);
        Long missedCards = cardScoreRepository.countByUserId(userId);
        Long customCards = customCardRepository.countByUserId(userId);

        homeInfoDto.setSavedCardNumber(savedCards);
        homeInfoDto.setMissedCardNumber(missedCards);
        homeInfoDto.setCustomCardNumber(customCards);
    }

}
