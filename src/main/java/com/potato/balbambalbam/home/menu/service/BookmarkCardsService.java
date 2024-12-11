package com.potato.balbambalbam.home.menu.service;

import com.potato.balbambalbam.data.entity.CardBookmark;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.home.menu.dto.BookmarkCardResponseDto;
import com.potato.balbambalbam.home.menu.dto.CardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkCardsService {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;
    private final CardScoreRepository cardScoreRepository;

    public BookmarkCardResponseDto getCards(Long userId) {
        List<Long> bookmarkCardIdList = cardBookmarkRepository.findAllByUserId(userId).stream().map(CardBookmark::getCardId).toList();

        List<CardDto> cardList = new ArrayList<>();
        cardRepository.findByCardIdIn(bookmarkCardIdList).forEach(card -> {
            Long cardId = card.getCardId();
            cardList.add(new CardDto(card.getCardId(), card.getText(),
                    card.getCardPronunciation(),
                    true,
                    cardWeakSoundRepository.existsByCardIdAndUserId(cardId, userId),
                    cardScoreRepository.findByCardIdAndUserId(cardId, userId)
                            .map(CardScore::getHighestScore)
                            .orElse(0)));
        });

        return new BookmarkCardResponseDto(cardList);
    }
}
