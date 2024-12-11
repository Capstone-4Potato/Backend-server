package com.potato.balbambalbam.card.cardFeedback.service;

import com.potato.balbambalbam.card.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.card.cardFeedback.dto.AiFeedbackResponseDto;
import com.potato.balbambalbam.card.cardFeedback.dto.UserFeedbackRequestDto;
import com.potato.balbambalbam.card.cardFeedback.dto.UserFeedbackResponseDto;
import com.potato.balbambalbam.card.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.data.repository.TodayCardRepository;
import com.potato.balbambalbam.exception.CardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TodayCardFeedbackService {
    private final TodayCardRepository todayCardRepository;
    private final AiCardFeedbackService aiCardFeedbackService;

    public UserFeedbackResponseDto postUserFeedback(UserFeedbackRequestDto userFeedbackRequestDto, Long cardId, Long userId) {
        //인공지능서버와 통신
        AiFeedbackResponseDto aiFeedbackResponseDto = getAiFeedbackResponseDto(userFeedbackRequestDto, cardId, userId);

        //학습카드 추천
        int score = aiFeedbackResponseDto.getUserAccuracy();
        Map<String, CardInfoResponseDto> recommendCard = new HashMap<>();
        if (score == 100) {
            recommendCard.put("Perfect", new CardInfoResponseDto());
        } else {
            recommendCard.put("Try Again", new CardInfoResponseDto());
        }

        return setUserFeedbackResponseDto(aiFeedbackResponseDto, recommendCard, cardId);
    }

    protected AiFeedbackResponseDto getAiFeedbackResponseDto(UserFeedbackRequestDto userFeedbackRequestDto, Long cardId, Long userId) {
        AiFeedbackRequestDto aiFeedbackRequestDto = createAiFeedbackRequestDto(userFeedbackRequestDto, cardId, userId);
        AiFeedbackResponseDto aiFeedbackResponseDto = aiCardFeedbackService.postAiFeedback(aiFeedbackRequestDto);

        return aiFeedbackResponseDto;
    }

    protected AiFeedbackRequestDto createAiFeedbackRequestDto(UserFeedbackRequestDto userFeedbackRequestDto, Long cardId, Long userId) {
        String pronunciation = todayCardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getText();
        AiFeedbackRequestDto aiFeedbackRequestDto = new AiFeedbackRequestDto();

        aiFeedbackRequestDto.setUserAudio(userFeedbackRequestDto.getUserAudio());
        aiFeedbackRequestDto.setCorrectAudio(userFeedbackRequestDto.getCorrectAudio());
        aiFeedbackRequestDto.setPronunciation(pronunciation);

        return aiFeedbackRequestDto;
    }


    protected UserFeedbackResponseDto setUserFeedbackResponseDto(AiFeedbackResponseDto aiFeedback, Map<String, CardInfoResponseDto> recommendCard, Long cardId) {
        //사용자 오디오, TTS 오디오
        UserFeedbackResponseDto.UserAudio userAudio = new UserFeedbackResponseDto.UserAudio(aiFeedback.getUserText(), aiFeedback.getUserAmplitude());
        UserFeedbackResponseDto.CorrectAudio correctAudio = new UserFeedbackResponseDto.CorrectAudio(aiFeedback.getCorrectAudio(), aiFeedback.getCorrectAmplitude());

        //객체 생성 및 설정
        UserFeedbackResponseDto userFeedbackResponseDto = new UserFeedbackResponseDto();
        userFeedbackResponseDto.setCardId(cardId);
        userFeedbackResponseDto.setUserScore(aiFeedback.getUserAccuracy());
        userFeedbackResponseDto.setUserText(aiFeedback.getUserText());
        userFeedbackResponseDto.setRecommendCard(recommendCard);
        userFeedbackResponseDto.setUserAudio(userAudio);
        userFeedbackResponseDto.setCorrectAudio(correctAudio);
        userFeedbackResponseDto.setMistakenIndexes(aiFeedback.getUserMistakenIndexes());

        return userFeedbackResponseDto;
    }
}
