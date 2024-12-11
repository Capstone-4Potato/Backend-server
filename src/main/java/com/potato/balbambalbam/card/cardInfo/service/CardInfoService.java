package com.potato.balbambalbam.card.cardInfo.service;

import com.potato.balbambalbam.card.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardVoice;
import com.potato.balbambalbam.data.entity.PronunciationPicture;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.exception.VoiceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardInfoService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;
    private final CardVoiceRepository cardVoiceRepository;
    private final PronunciationPictureRepository pronunciationPictureRepository;

    public CardInfoResponseDto getCardInfo(Long userId, Long cardId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User가 존재하지 않습니다"));

        String cardVoice = getCardVoice(cardId, user.getGender(), user.getAge());
        return getCardInfoResponseDto(userId, cardId, cardVoice);
    }

    protected CardInfoResponseDto getCardInfoResponseDto(Long userId, Long cardId, String voice) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("잘못된 URL 요청입니다"));

        CardInfoResponseDto cardInfoResponseDto = CardInfoResponseDto.builder()
                .id(card.getCardId())
                .text(card.getText())
                .cardPronunciation(card.getCardPronunciation())
                .cardTranslation(card.getCardTranslation())
                .isWeakCard(cardWeakSoundRepository.existsByCardIdAndUserId(cardId, userId))
                .correctAudio(voice)
                .build();

        if (card.getCategoryId() == 1 || card.getCategoryId() == 3) {
            PronunciationPicture pronunciationInfo = getPictureAndExplanation(card);
            cardInfoResponseDto.setExplanation(pronunciationInfo.getExplanation());
            cardInfoResponseDto.setPictureUrl("/images/" + pronunciationInfo.getPhonemeId() + ".png");
        }

        return cardInfoResponseDto;
    }

    protected PronunciationPicture getPictureAndExplanation(Card card) {
        Long phonemeId = null;
        //모음인 경우
        if (card.getCardId() <= 27) {
            phonemeId = card.getPhonemesMap().get(1);
        }
        //자음인 경우
        else if(card.getCardId() <= 46) {
            phonemeId = card.getPhonemesMap().get(0);
        }
        else if(card.getCardId() >= 92 && card.getCardId() <= 135) {
            phonemeId = card.getPhonemesMap().get(2);
        }
        return pronunciationPictureRepository.findByPhonemeId(phonemeId)
                .orElseThrow(() -> new IllegalArgumentException("음절 설명 찾기에 실패했습니다"));

    }

    protected String getCardVoice(Long cardId, byte gender, int age) {
        CardVoice cardVoice = cardVoiceRepository.findById(cardId)
                .orElseThrow(() -> new VoiceNotFoundException("TTS 음성이 존재하지 않습니다"));

        String voice = null;
        switch (gender) {
            // 남자
            case (0): {
                if (age <= 14) {// 아이
                    voice = cardVoice.getChildMale();
                } else if (age <= 40) { // 청년
                    voice = cardVoice.getAdultMale();
                } else { //중장년
                    voice = cardVoice.getElderlyMale();
                }
                break;
            }
            // 여자
            case (1): {
                if (age <= 14) { // 아이
                    voice = cardVoice.getChildFemale();
                } else if (age <= 40) { // 청년
                    voice = cardVoice.getAdultFemale();
                } else { // 중장년
                    voice = cardVoice.getElderlyFemale();
                }
                break;
            }
        }

        return voice;
    }
}