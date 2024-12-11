package com.potato.balbambalbam.main.customCard.service;

import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.main.cardList.service.AiEngTranslationService;
import com.potato.balbambalbam.main.customCard.dto.CustomCardResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CustomCardService {

    private final CustomCardRepository customCardRepository;
    private final UserRepository userRepository;
    private final AiPronunciationService aiPronunciationService;
    private final AiEngTranslationService aiEngTranslationService;

    public CustomCardResponseDto createCustomCardIfPossible (String text, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다"));

        CustomCard customCard = createCustomCard(text, userId);

        return createCustomCardResponse(customCard);
    }

    protected CustomCardResponseDto createCustomCardResponse (CustomCard customCard){
        CustomCardResponseDto customCardResponse = new CustomCardResponseDto();
        customCardResponse.setId(customCard.getId());
        customCardResponse.setText(customCard.getText());
        customCardResponse.setEngPronunciation(customCard.getEngPronunciation());
        customCardResponse.setEngTranslation(customCard.getEngTranslation());

        return customCardResponse;
    }

    public boolean deleteCustomCard(Long userId, Long cardId){
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        CustomCard customCard = customCardRepository.findCustomCardByIdAndUserId(cardId, userId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다"));

        customCardRepository.delete(customCard);

        if(customCardRepository.findById(cardId).isPresent()){
            return false;
        }

        return true;
    }


    protected CustomCard createCustomCard (String text, Long userId){
        CustomCard customCard = new CustomCard();
        customCard.setText(text);
        String engPronunciation = aiPronunciationService.getEngPronunciation(text).getEngPronunciation();
        customCard.setEngPronunciation(engPronunciation);
        customCard.setUserId(userId);
        customCard.setIsBookmarked(false);
        customCard.setEngTranslation(aiEngTranslationService.getEngTranslation(customCard.getText()).getEngTranslation());

        return customCardRepository.save(customCard);
    }


}
