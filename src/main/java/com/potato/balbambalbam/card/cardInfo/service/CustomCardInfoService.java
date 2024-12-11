package com.potato.balbambalbam.card.cardInfo.service;

import com.potato.balbambalbam.card.cardInfo.dto.AiTtsRequestDto;
import com.potato.balbambalbam.card.cardInfo.dto.AiTtsResponseDto;
import com.potato.balbambalbam.card.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomCardInfoService {

    private final UserRepository userRepository;
    private final AiTtsService aiTtsService;
    private final CustomCardRepository customCardRepository;

    public CardInfoResponseDto getCustomCardInfo(Long userId, Long cardId) {
        return getCardInfoResponseDto(userId, cardId);
    }

    /**
     * 커스텀 카드 text 추출
     */
    protected CardInfoResponseDto getCardInfoResponseDto(Long userId, Long cardId) {
        CustomCard card = customCardRepository.findCustomCardByIdAndUserId(cardId, userId).orElseThrow(() -> new CardNotFoundException("잘못된 URL 요청입니다"));

        CardInfoResponseDto cardInfoResponseDto = CardInfoResponseDto.builder()
                .id(card.getId())
                .text(card.getText())
                .cardPronunciation(card.getEngPronunciation())
                .cardTranslation(card.getEngTranslation())
                .isWeakCard(false)
                .correctAudio(createCardVoice(getAiTtsRequestDto(userId, card.getText())))
                .build();

        return cardInfoResponseDto;
    }

    protected AiTtsRequestDto getAiTtsRequestDto(Long userId, String text) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        Integer age = user.getAge();
        Byte gender = user.getGender();

        return new AiTtsRequestDto(age, gender, text);
    }

    protected String createCardVoice(AiTtsRequestDto aiTtsRequestDto) {
        AiTtsResponseDto aiTtsResponseDto = aiTtsService.getTtsVoice(aiTtsRequestDto);
        return aiTtsResponseDto.getCorrectAudio();
    }
}
