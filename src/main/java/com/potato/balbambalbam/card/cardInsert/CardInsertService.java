package com.potato.balbambalbam.card.cardInsert;

import com.potato.balbambalbam.card.tts.UpdateAllTtsService;
import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.home.learningCourse.service.UpdateEngPronunciationService;
import com.potato.balbambalbam.home.learningCourse.service.UpdateEngTranslationService;
import com.potato.balbambalbam.home.learningCourse.service.UpdatePhonemeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardInsertService {
    private final CardRepository cardRepository;
    private final UpdatePhonemeService updatePhonemeService;
    private final UpdateEngPronunciationService updateEngPronunciationService;
    private final UpdateEngTranslationService updateEngTranslationService;
    private final UpdateAllTtsService updateAllTtsService;

    public int updateCardRecordList() {
        List<Card> cardList = cardRepository.findAllByCategoryId(25L);

        cardList.forEach(card -> {
            if (isNeedUpdate(card)) {
                updateCardRecord(card);
                cardRepository.save(card);
            }
        });

        return cardList.size();
    }

    @Transactional
    protected void updateCardRecord(Card card) {
        updatePhonemeService.updateCardPhonemeColumn(card);
        updateEngPronunciationService.updateEngPronunciation(card);
        updateEngTranslationService.updateEngTranslation(card);
        updateAllTtsService.updateCardVoice(card);
    }

    protected boolean isNeedUpdate(Card card) {
        if (card.getCardTranslation() == null) {
            return true;
        }
        return false;
    }
}