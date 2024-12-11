package com.potato.balbambalbam.card.cardInsert;

import com.potato.balbambalbam.card.tts.UpdateAllTtsService;
import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.TodayCard;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardVoiceRepository;
import com.potato.balbambalbam.data.repository.TodayCardRepository;
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
    private final CardVoiceRepository cardVoiceRepository;
    private final UpdateEngPronunciationService updateEngPronunciationService;
    private final UpdateEngTranslationService updateEngTranslationService;
    private final UpdateAllTtsService updateAllTtsService;
    private final TodayCardRepository todayCardRepository;

    public int updateCardRecordList() {
        List<Card> cardList = cardRepository.findAll();

        cardList.forEach(card -> {
            if (isNeedUpdate(card)) {
                updateCardRecord(card);
                cardRepository.save(card);
            }
        });

        return cardList.size();
    }

    public int updateTodayCardRecordList() {
        List<TodayCard> cardList = todayCardRepository.findAll();

        cardList.forEach(card -> {
            if (isTodayCardVoiceNeedUpdate(card)) {
                updateTodayCard(card);
            }
        });

        return cardList.size();
    }

    protected void updateTodayCard(TodayCard card) {
        updateAllTtsService.updateTodayCardVoice(card);
    }

    protected boolean isTodayCardVoiceNeedUpdate(TodayCard card) {
        if (card.getChildMale() == null) {
            return true;
        }
        return false;
    }

    @Transactional
    protected void updateCardRecord(Card card) {
        updatePhonemeService.updateCardPhonemeColumn(card);
        updateEngPronunciationService.updateEngPronunciation(card);
        updateEngTranslationService.updateEngTranslation(card);
        updateAllTtsService.updateCardVoice(card);
    }

    protected boolean isNeedUpdate(Card card) {
        if (card.getCardPronunciation() ==  null || card.getCardTranslation() == null || card.getCardPronunciation() == null || cardVoiceRepository.existsById(card.getCardId())) {
            return true;
        }
        return false;
    }
}