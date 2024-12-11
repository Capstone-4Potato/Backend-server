package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.main.customCard.service.AiPronunciationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateEngPronunciationService {
    private final CardRepository cardRepository;
    private final AiPronunciationService aiPronunciationService;
    @Transactional
    public void updateEngPronunciation(){
        List<Card> cardList = cardRepository.findAll();

        cardList.stream().forEach(card -> {
            Card foundCard  = cardRepository.findById(card.getId()).get();
            if(isProceedCard(card)){
                String text = foundCard.getText();
                foundCard.setEngPronunciation(aiPronunciationService.getEngPronunciation(text).getEngPronunciation());
                cardRepository.save(foundCard);
                log.info(foundCard + "update");
            }
        });
    }
    protected boolean isProceedCard(Card card){
        if(card.getEngPronunciation() != null){
            return false;
        }
        return true;
    }
}
