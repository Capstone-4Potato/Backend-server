package com.potato.balbambalbam.card.cardInfo.service;

import com.potato.balbambalbam.card.cardInfo.dto.AiTtsRequestDto;
import com.potato.balbambalbam.card.cardInfo.dto.AiTtsResponseDto;
import com.potato.balbambalbam.card.cardInfo.dto.TodayCardInfoResponseDto;
import com.potato.balbambalbam.data.entity.CardVoice;
import com.potato.balbambalbam.data.entity.TodayCard;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.TodayCardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.exception.VoiceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TodayCardInfoService {

    private final TodayCardRepository todayCardRepository;
    private final UserRepository userRepository;

    public TodayCardInfoResponseDto getTodayCardInfo(Long userId, Long cardId) {
        TodayCard todayCard = todayCardRepository.findById(cardId).get();

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        String voice = getCardVoice(todayCard, user.getGender(), user.getAge());
        return getCardInfoResponseDto(userId, cardId, voice);
    }

    protected TodayCardInfoResponseDto getCardInfoResponseDto(Long userId, Long cardId, String voice) {
        TodayCard card = todayCardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("잘못된 URL 요청입니다"));

        TodayCardInfoResponseDto todayCardInfoResponseDto = TodayCardInfoResponseDto.builder()
                .id(card.getCardId())
                .text(card.getText())
                .cardPronunciation(card.getCardPronunciation())
                .cardSummary(card.getCardSummary())
                .correctAudio(voice)
                .build();
        return todayCardInfoResponseDto;
    }

    protected String getCardVoice(TodayCard todayCard, byte gender, int age) {
        String voice = null;
        switch (gender) {
            // 남자
            case (0): {
                if (age <= 14) {// 아이
                    voice = todayCard.getChildMale();
                } else if (age <= 40) { // 청년
                    voice = todayCard.getAdultMale();
                } else { //중장년
                    voice = todayCard.getElderlyMale();
                }
                break;
            }
            // 여자
            case (1): {
                if (age <= 14) { // 아이
                    voice = todayCard.getChildFemale();
                } else if (age <= 40) { // 청년
                    voice = todayCard.getAdultFemale();
                } else { // 중장년
                    voice = todayCard.getElderlyFemale();
                }
                break;
            }
        }

        return voice;
    }
}