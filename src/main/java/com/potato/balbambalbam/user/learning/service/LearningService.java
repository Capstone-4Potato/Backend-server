package com.potato.balbambalbam.user.learning.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardScoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LearningService {

    private final CardScoreRepository cardScoreRepository;
    private final CardRepository cardRepository;

    public LearningService(CardScoreRepository cardScoreRepository,
                           CardRepository cardRepository){
        this.cardScoreRepository = cardScoreRepository;
        this.cardRepository = cardRepository;
    }

    public List<CardScore> findCardScoresByUserId(Long userId){
        return cardScoreRepository.findByUserId(userId);
    }

    public Map<Long, Card> findCardsByIds(List<Long> cardIds){
        List<Card> cards = cardRepository.findAllById(cardIds);
        return cards.stream().collect(Collectors.toMap(Card::getId, card -> card));
    }

    public List<Card> findAllCards(){
        return cardRepository.findAll();
    }

}
