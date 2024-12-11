package com.potato.balbambalbam.home.customCard.service;

import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.home.customCard.dto.CustomCardResponseDto;
import com.potato.balbambalbam.home.learningCourse.service.AiTranslationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CustomCardService {

    private final CustomCardRepository customCardRepository;
    private final UserRepository userRepository;
    private final AiPronunciationService aiPronunciationService;
    private final AiTranslationService aiTranslationService;

    public CustomCardResponseDto createCustomCardIfPossible(String text, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다"));

        CustomCard customCard;
        if(!isEnglish(text)) {
            customCard = createKoreanInput(text, userId);
        } else {
            customCard = createEnglishInput(text, userId);
        }

        return createCustomCardResponse(customCard);
    }

    public static boolean isEnglish(String text) {
        return text.matches(".*[a-zA-Z]+.*");
    }

    protected CustomCardResponseDto createCustomCardResponse(CustomCard customCard) {
        CustomCardResponseDto customCardResponse = new CustomCardResponseDto();
        customCardResponse.setId(customCard.getId());
        customCardResponse.setText(customCard.getText());
        customCardResponse.setEngPronunciation(customCard.getEngPronunciation());
        customCardResponse.setEngTranslation(customCard.getEngTranslation());
        customCardResponse.setCreatedAt(customCard.getTimeStamp());

        return customCardResponse;
    }

    protected CustomCard createKoreanInput(String text, Long userId) {
        String engPronunciation = aiPronunciationService.getEngPronunciation(text).getEngPronunciation();
        String engTranslation = aiTranslationService.getEngTranslation(text).getEngTranslation();
        return createCustomCard(text, engPronunciation, engTranslation, userId);
    }

    protected CustomCard createEnglishInput(String englishText, Long userId) {
        String text = aiTranslationService.getKorTranslation(englishText).getKorTranslation();
        String engPronunciation = aiPronunciationService.getEngPronunciation(text).getEngPronunciation();
        return createCustomCard(text, engPronunciation, englishText, userId);
    }

    @Transactional
    protected CustomCard createCustomCard(String text, String engPronunciation, String engTranslation, Long userId) {
        CustomCard customCard = new CustomCard();
        customCard.setText(text);
        customCard.setEngPronunciation(engPronunciation);
        customCard.setUserId(userId);
        customCard.setIsBookmarked(false);
        customCard.setEngTranslation(engTranslation);
        customCard.setTimeStamp(LocalDateTime.now());
        customCard.setHighestScore(0);

        return customCardRepository.save(customCard);
    }

    public boolean deleteCustomCard(Long userId, Long cardId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        CustomCard customCard = customCardRepository.findCustomCardByIdAndUserId(cardId, userId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다"));

        customCardRepository.delete(customCard);

        if (customCardRepository.findById(cardId).isPresent()) {
            return false;
        }

        return true;
    }
}