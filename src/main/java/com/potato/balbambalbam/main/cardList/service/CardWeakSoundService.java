package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardWeakSound;
import com.potato.balbambalbam.data.entity.UserWeakSound;
import com.potato.balbambalbam.data.repository.BulkRepository;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.UserWeakSoundRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardWeakSoundService {
    private final BulkRepository bulkRepository;
    private final CardRepository cardRepository;
    private final UserWeakSoundRepository userWeakSoundRepository;
    /**
     * 취약음 갱신 시 user weaksound table update
     * @param userId
     * @return
     */
    @Transactional
    public String updateCardWeakSound(Long userId){
        //card weaksound 테이블 해당 userId 행 전부 삭제
        bulkRepository.deleteAllByUserId(userId);

        List<Card> cardList = getCardListWithoutSentence();

        List<Long> phonemeList = getPhonemeList(userId);

        List<CardWeakSound> cardWeakSoundList = new ArrayList<>();
        cardList.forEach(card -> {
            List<Long> phonemes = card.getPhonemesMap();
            if(!Collections.disjoint(phonemes, phonemeList)){
                cardWeakSoundList.add(new CardWeakSound(userId ,card.getId()));
            }
        });
        bulkRepository.saveAll(cardWeakSoundList);

        return "카드 취약음 갱신 성공";
    }

    protected List<Card> getCardListWithoutSentence(){
        List<Long> categoryIds = Arrays.asList(
                5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L,
                14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L,
                23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L
        );
        List<Card> cardList = cardRepository.findByCategoryIdIn(categoryIds);

        return cardList;
    }

    protected List<Long> getPhonemeList(Long userId){
        List<UserWeakSound> userWeakSoundList = userWeakSoundRepository.findAllByUserId(userId);
        List<Long> phonemeList = new ArrayList<>();

        userWeakSoundList.forEach(userWeakSound -> {
            phonemeList.add(userWeakSound.getUserPhoneme());
        });

        return phonemeList;
    }
}
