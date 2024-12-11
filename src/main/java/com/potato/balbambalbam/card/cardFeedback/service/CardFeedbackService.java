package com.potato.balbambalbam.card.cardFeedback.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.potato.balbambalbam.card.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.card.cardFeedback.dto.AiFeedbackResponseDto;
import com.potato.balbambalbam.card.cardFeedback.dto.UserFeedbackRequestDto;
import com.potato.balbambalbam.card.cardFeedback.dto.UserFeedbackResponseDto;
import com.potato.balbambalbam.card.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.card.cardInfo.service.CardInfoService;
import com.potato.balbambalbam.data.entity.*;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.home.learningCourse.service.UpdatePhonemeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CardFeedbackService {
    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;
    private final AiCardFeedbackService aiCardFeedbackService;
    private final PhonemeRepository phonemeRepository;
    private final CardInfoService cardInfoService;

    public UserFeedbackResponseDto postUserFeedback(UserFeedbackRequestDto userFeedbackRequestDto, Long userId, Long cardId) {
        Long categoryId = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getCategoryId();
        //인공지능서버와 통신
        AiFeedbackResponseDto aiFeedbackResponseDto = getAiFeedbackResponseDto(userFeedbackRequestDto, cardId);

        //점수 업데이트
        if(categoryId == 1L || categoryId == 3L) {
            updateScoreIfLarger(userId, cardId, 100);
            aiFeedbackResponseDto.setUserAccuracy(100);
        } else{
            updateScoreIfLarger(userId, cardId, aiFeedbackResponseDto.getUserAccuracy());
        }

        //학습카드 추천
        Map<String, CardInfoResponseDto> recommendCard = createRecommendCard(userId, aiFeedbackResponseDto, categoryId);

        return setUserFeedbackResponseDto(cardId, aiFeedbackResponseDto, recommendCard);
    }

    /**
     * Ai 통신
     *
     * @param aiFeedbackRequest
     * @param cardId
     * @return
     * @throws JsonProcessingException
     */
    protected AiFeedbackResponseDto getAiFeedbackResponseDto(UserFeedbackRequestDto aiFeedbackRequest, Long cardId) {
        AiFeedbackRequestDto aiFeedbackRequestDto = createAiFeedbackRequestDto(aiFeedbackRequest, cardId);
        AiFeedbackResponseDto aiFeedbackResponseDto = aiCardFeedbackService.postAiFeedback(aiFeedbackRequestDto);

        return aiFeedbackResponseDto;
    }

    protected AiFeedbackRequestDto createAiFeedbackRequestDto(UserFeedbackRequestDto userFeedbackRequestDto, Long cardId) {
        String pronunciation = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getText();
        AiFeedbackRequestDto aiFeedbackRequestDto = new AiFeedbackRequestDto();

        aiFeedbackRequestDto.setUserAudio(userFeedbackRequestDto.getUserAudio());
        aiFeedbackRequestDto.setCorrectAudio(userFeedbackRequestDto.getCorrectAudio());
        aiFeedbackRequestDto.setPronunciation(pronunciation);

        return aiFeedbackRequestDto;
    }

    /**
     * 사용자 최고 점수 업데이트
     *
     * @param userId
     * @param cardId
     * @param userScore
     */
    @Transactional
    public void updateScoreIfLarger(Long userId, Long cardId, Integer userScore) {
        Optional<CardScore> optionalCardScore = cardScoreRepository.findByCardIdAndUserId(cardId, userId);

        if (optionalCardScore.isPresent()) {
            CardScore cardScore = optionalCardScore.get();
            if (cardScore.getHighestScore() < userScore) {
                cardScore.setHighestScore(userScore);
                cardScore.setTimeStamp(LocalDateTime.now());
                cardScoreRepository.save(cardScore);
            }
        } else {
            cardScoreRepository.save(new CardScore(userScore, userId, cardId, LocalDateTime.now()));
        }
    }

    /**
     * 추천학습 카드 생성
     *
     * @param aiFeedbackResponseDto
     * @param categoryId
     * @return
     */
    protected Map<String, CardInfoResponseDto> createRecommendCard(Long userId, AiFeedbackResponseDto aiFeedbackResponseDto, Long categoryId) {
        Map<String, CardInfoResponseDto> recommendCard = new HashMap<>();


        //1. 100점인 경우
        if (aiFeedbackResponseDto.getUserAccuracy() == 100) {
            recommendCard.put("Perfect", new CardInfoResponseDto());
            return recommendCard;
        }

        //2. 100점 아니고 한글자인 경우
        if(categoryId == 1L || categoryId == 3L) {
            recommendCard.put("Try Again", new CardInfoResponseDto());
            return recommendCard;
        }

        //3. 100점이 아닌 경우
        return getWordRecommendCards(userId, aiFeedbackResponseDto.getRecommendedPronunciations(), aiFeedbackResponseDto.getRecommendedLastPronunciations());

    }

    protected Map<String, CardInfoResponseDto> getWordRecommendCards(Long userId, List<String> recommendedPronunciations, List<String> recommendedLastPronunciations) {
        Map<String, CardInfoResponseDto> recommendCard = new HashMap<>();

        //4. 틀린게 4개 이상인 경우
        if(recommendedPronunciations.size() + recommendedLastPronunciations.size() > 4) {
            recommendCard.put("Try Again", new CardInfoResponseDto());
            return recommendCard;
        }

        List<Card> cardList = cardRepository.findAllByCategoryId(1L);
        List<Card> finalCardList = cardRepository.findAllByCategoryId(3L);
        if(!recommendedPronunciations.isEmpty()) {
            for(String pronunciation : recommendedPronunciations) {
                Long phonemeId = phonemeRepository.findPhonemeByTextOrderById(pronunciation).get(0).getId();
                Long cardId = cardList.stream().filter(c -> c.getPhonemesMap().contains(phonemeId)).findAny().get().getCardId();
                String cardDescription;
                String text = phonemeRepository.findById(phonemeId).get().getText();
                if(cardId <= 27L) {
                    cardDescription = "Vowel";
                } else {
                    cardDescription = "Consonant";
                }
                recommendCard.put(cardDescription + text, cardInfoService.getCardInfo(userId, cardId));
            }
        }

        if(!recommendedLastPronunciations.isEmpty()) {
            for(String pronunciation : recommendedLastPronunciations) {
                Long phonemeId = phonemeRepository.findPhonemeByTextAndTypeOrderById(pronunciation, 2L).get(0).getId();
                Long cardId = finalCardList.stream().filter(c -> c.getPhonemesMap().contains(phonemeId)).findAny().get().getCardId();
                String text = phonemeRepository.findById(phonemeId).get().getText();
                recommendCard.put("Final Consonant" + text, cardInfoService.getCardInfo(userId, cardId));
            }
        }

        return recommendCard;
    }
    /**
     * userFeedBackResponseDto 생성
     *
     * @param aiFeedback
     * @param recommendCard
     * @return
     */
    protected UserFeedbackResponseDto setUserFeedbackResponseDto(Long cardId, AiFeedbackResponseDto aiFeedback, Map<String, CardInfoResponseDto> recommendCard) {
        //사용자 오디오, TTS 오디오
        UserFeedbackResponseDto.UserAudio userAudio = new UserFeedbackResponseDto.UserAudio(aiFeedback.getUserAudio(), aiFeedback.getUserAmplitude());
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