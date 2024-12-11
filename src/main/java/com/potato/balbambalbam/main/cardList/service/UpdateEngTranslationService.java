package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateEngTranslationService {
    private final CardRepository cardRepository;
    private final AiEngTranslationService aiEngTranslationService;
    @Transactional
    public void updateEngTranslation(){
        List<Card> cardList = cardRepository.findAll();

        cardList.stream().forEach(card -> {
            Card foundCard  = cardRepository.findById(card.getId()).get();
            if(isProceedCard(card)){
                String text = foundCard.getText();
                foundCard.setEngTranslation(aiEngTranslationService.getEngTranslation(text).getEngTranslation());
                cardRepository.save(foundCard);
                log.info(foundCard + "update engTranslation");
            }
        });
    }
    protected boolean isProceedCard(Card card){
        if(card.getEngTranslation() != null){
            return false;
        }
        return true;
    }
}
