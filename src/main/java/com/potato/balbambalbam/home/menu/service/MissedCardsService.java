package com.potato.balbambalbam.home.menu.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardScoreRepository;
import com.potato.balbambalbam.data.repository.CardWeakSoundRepository;
import com.potato.balbambalbam.home.menu.dto.CardDto;
import com.potato.balbambalbam.home.menu.dto.MissedCardResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissedCardsService {
    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;

    public MissedCardResponseDto getCards(Long userId) {
        return new MissedCardResponseDto(createCardDtoListForCategory(userId));
    }

    protected Map<String, List<CardDto>> createCardDtoListForCategory(Long userId) {
        List<CardScore> missedCardScoreList = cardScoreRepository.findByUserId(userId).stream().filter(cardScore -> cardScore.getHighestScore() != 100).toList();
        Map<String, List<CardScore>> dateCardsMap = missedCardScoreList.stream()
                .collect(Collectors.groupingBy(
                        card -> card.getTimeStamp()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        TreeMap::new,  // TreeMap을 사용하여 키(날짜)를 자동으로 정렬
                        Collectors.toList()
                ));

        Map<String, List<CardDto>> missedCardList = new TreeMap<>();
        for (Map.Entry<String, List<CardScore>> entry : dateCardsMap.entrySet()) {
            String date = entry.getKey();
            List<CardScore> value = entry.getValue();
            missedCardList.put(date, convertToCardDto(value, userId));
        }

        return missedCardList;
    }


    protected List<CardDto> convertToCardDto(List<CardScore> cardScoreList, Long userId) {
        List<Long> cardIdList = cardScoreList.stream().map(CardScore::getCardId).toList();
        List<Card> cardList = cardRepository.findByCardIdIn(cardIdList);

        List<CardDto> cardDtoList = new ArrayList<>();
        cardList.forEach(
                card -> {
                    Long cardId = card.getCardId();
                    cardDtoList.add(new CardDto(card.getCardId(), card.getText(),
                            card.getCardPronunciation(),
                            true,
                            cardWeakSoundRepository.existsByCardIdAndUserId(cardId, userId),
                            cardScoreRepository.findByCardIdAndUserId(cardId, userId)
                                    .map(CardScore::getHighestScore)
                                    .orElse(0)));
                }
        );


        return cardDtoList;
    }
}
