package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardBookmark;
import com.potato.balbambalbam.data.entity.Category;
import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.CardNotFoundException;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CardList Service 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class CardListServiceTest {

    @InjectMocks
    CardListService cardListService;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    CardRepository cardRepository;
    @Mock
    CardBookmarkRepository cardBookmarkRepository;
    @Mock
    CardWeakSoundRepository cardWeakSoundRepository;
    @Mock
    CardScoreRepository cardScoreRepository;
    @Mock
    CustomCardRepository customCardRepository;
    @Mock
    PronunciationPictureRepository pronunciationPictureRepository;

    @BeforeEach
    void beforeEach(){
        //card 생성
        Card card1 = new Card("가을", "가을", 15L);
        card1.setEngPronunciation("gaeul");
        card1.setPhonemesMap(new LinkedList<>(Arrays.asList(1L, 19L, 8L, 23L, 43L)));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));

        Card card2 = new Card("고양이", "고양이", 15L);
        card2.setEngPronunciation("");
        card2.setPhonemesMap(new LinkedList<>(Arrays.asList(1L, 21L, 8L, 27L, 46L, 24L)));

        when(cardRepository.findById(2L)).thenReturn(Optional.of(card2));
    }

    @Nested
    @DisplayName("요청한 카드리스트 생성")
    class testGetCardsByCategory{

    }

    @Test @DisplayName("북마크 토글")
    void testToggleCardBookmark(){
        //given
        CardBookmark cardBookmark = new CardBookmark(1L, 1L);

        doReturn(true).when(cardBookmarkRepository).existsByCardIdAndUserId(1L, 1L);
        doReturn(false).when(cardBookmarkRepository).existsByCardIdAndUserId(2L, 1L);
        doThrow(CardNotFoundException.class).when(cardRepository).findById(3L);

        //when
        String toggleCardBookmark1 = cardListService.toggleCardBookmark(1L, 1L);
        String toggleCardBookmark2 = cardListService.toggleCardBookmark(2L, 1L);

        //then
        assertThat(toggleCardBookmark1).isEqualTo("1번 카드 북마크 제거");
        assertThat(toggleCardBookmark2).isEqualTo("2번 카드 북마크 추가");
        assertThrows(CardNotFoundException.class, () -> cardListService.toggleCardBookmark(3L, 1L));
    }
}