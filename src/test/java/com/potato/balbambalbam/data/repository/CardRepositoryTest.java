package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.Card;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CardRepository 단위 테스트
 */
@DataJpaTest
class CardRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    CardRepository cardRepository;

    /**
     * 1. Given : 테스트 데이터 설정
     */
    @BeforeEach
    void setUp() {
        testEntityManager.clear();//컨텍스트 상태 초기화

        Card card1 = new Card("테스트1", "테스트발음1", 1L);
        Card card2 = new Card("찾아보셈", "테스트발음2", 2L);
        Card card3 = new Card("테스트3", "테스트발음3", 3L);
        Card card4 = new Card("테스트4", "테스트발음4", 1L);
        Card card5 = new Card("테스트5", "테스트발음5", 2L);
        Card card6 = new Card("찾아보셈", "테스트발음7", 3L);
        Card card7 = new Card("테스트7", "테스트발음8", 1L);

        testEntityManager.persist(card1);//데이터베이스 저장
        testEntityManager.persist(card2);
        testEntityManager.persist(card3);
        testEntityManager.persist(card4);
        testEntityManager.persist(card5);
        testEntityManager.persist(card6);
        testEntityManager.persist(card7);

        testEntityManager.flush();//데이터베이스 반영
    }

    @DisplayName("cardList 전체 찾기")
    @Test
    void findAllTest() {
        //When
        List<Card> cardList = cardRepository.findAll();
        //Then
        Assertions.assertThat(cardList.size()).isEqualTo(7);
    }

    @DisplayName("category Id에 따른 cardList 찾기")
    @Test
    void findAllByCategoryIdTest() {
        //When
        List<Card> cardList = cardRepository.findAllByCategoryId(1L);
        //Then
        Assertions.assertThat(cardList.getFirst().getCategoryId()).isEqualTo(1L);
        Assertions.assertThat(cardList.size()).isEqualTo(3);
    }


    @DisplayName("text에 따른 cardList 찾기")
    @Test
    void findByTextOrderByIdTest() {
        //When
        List<Card> cardList = cardRepository.findByTextOrderById("찾아보셈");

        //Then
        Assertions.assertThat(cardList.getFirst().getCategoryId()).isEqualTo(2L);
        Assertions.assertThat(cardList.getLast().getCategoryId()).isEqualTo(3L);
    }

}