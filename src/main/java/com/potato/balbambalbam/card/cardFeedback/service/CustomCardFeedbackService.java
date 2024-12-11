package com.potato.balbambalbam.card.cardFeedback.service;

import com.potato.balbambalbam.card.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.card.cardFeedback.dto.AiFeedbackResponseDto;
import com.potato.balbambalbam.card.cardFeedback.dto.UserFeedbackRequestDto;
import com.potato.balbambalbam.card.cardFeedback.dto.UserFeedbackResponseDto;
import com.potato.balbambalbam.card.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.data.repository.UserLevelRepository;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CustomCardFeedbackService {
    private final CustomCardRepository customCardRepository;
    private final AiCardFeedbackService aiCardFeedbackService;
    private final UserLevelRepository userLevelRepository;

    public UserFeedbackResponseDto postUserFeedback(UserFeedbackRequestDto userFeedbackRequestDto, Long cardId, Long userId) {
        //인공지능서버와 통신
        AiFeedbackResponseDto aiFeedbackResponseDto = getAiFeedbackResponseDto(userFeedbackRequestDto, cardId, userId);

        //점수 업데이트
        int score = aiFeedbackResponseDto.getUserAccuracy();
        updateScoreIfLarger(cardId, score);

        //학습카드 추천
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
        String pronunciation = customCardRepository.findCustomCardByIdAndUserId(cardId, userId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getText();
        AiFeedbackRequestDto aiFeedbackRequestDto = new AiFeedbackRequestDto();

        aiFeedbackRequestDto.setUserAudio(userFeedbackRequestDto.getUserAudio());
        aiFeedbackRequestDto.setCorrectAudio(userFeedbackRequestDto.getCorrectAudio());
        aiFeedbackRequestDto.setPronunciation(pronunciation);

        return aiFeedbackRequestDto;
    }


    /**
     * 점수 업데이트
     *
     * @param cardId
     * @param userScore
     */
    public void updateScoreIfLarger(Long cardId, Integer userScore) {
        CustomCard customCard = customCardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다"));

        if (customCard.getHighestScore() == null) {
            customCard.setHighestScore(userScore);
            customCardRepository.save(customCard);
        } else if (customCard.getHighestScore() < userScore) {
            customCard.setHighestScore(userScore);
            customCardRepository.save(customCard);
        }
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
