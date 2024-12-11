package com.potato.balbambalbam.review.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.entity.Category;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.CategoryNotFoundException;
import com.potato.balbambalbam.review.dto.CardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final CategoryRepository categoryRepository;
    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;
    private final CardBookmarkRepository cardBookmarkRepository;

    public List<CardDto> getCardsByCategory(String category, String subcategory,Long userId){
        Long requestCategory = getSubCategoryId(category, subcategory);
        List<CardDto> cardDtoList = createCardDtoListForCategory(requestCategory, userId);

        return cardDtoList;
    }

    protected Long getSubCategoryId(String category, String subcategory){
        //부모 카테고리 아이디 찾기
        Long parentId = categoryRepository.findByName(category).map(Category::getId).orElseThrow(()->new CategoryNotFoundException("잘못된 URL 요청입니다"));
        //하위 카테고리 아이디 찾기
        Long requestCategoryId = categoryRepository.findByNameAndParentId(subcategory, parentId).map(Category::getId).orElseThrow(()->new CategoryNotFoundException("잘못된 URL 요청입니다"));

        return requestCategoryId;
    }

    protected List<CardDto> createCardDtoListForCategory(Long categoryId, Long userId){
        List<Card> cardList = cardRepository.findAllByCategoryId(categoryId);
        List<CardDto> cardDtoList = new ArrayList<>();

        cardList.stream().forEach(card -> {
            if(isReviewCard(card, userId, categoryId)){
                cardDtoList.add(convertCardToDto(card, userId));
            }
        });

        Collections.sort(cardDtoList, (o1, o2) -> {
            if(o1.getCardScore()==o2.getCardScore()){
                return (int) (o1.getId() - o2.getId());
            }
            return o1.getCardScore() - o2.getCardScore();
        });

        return cardDtoList;
    }

    protected boolean isReviewCard(Card card, Long userId, Long categoryId){
        Optional<CardScore> cardScore = cardScoreRepository.findByCardIdAndUserId(card.getId(), userId);

        if(!cardScore.isPresent()){//값이 존재하지 않으면 학습하지 않은 것 => 복습카드 포함 X
            return false;
        }
        int highestScore = cardScore.get().getHighestScore();

        //학습한 경우 -> 100점 아니면 모두 복습카드로 포함
        if(highestScore < 100){
            return true;
        }

        return false;
    }

    protected CardDto convertCardToDto(Card card, Long userId){
        CardDto cardDto = new CardDto();

        Long cardId = card.getId();
        cardDto.setId(cardId);
        cardDto.setText(card.getText());

        cardDto.setCardScore(cardScoreRepository.findByCardIdAndUserId(cardId, userId).map(CardScore::getHighestScore).orElse(0));  //사용자 점수가 없으면 0점
        cardDto.setWeakCard(cardWeakSoundRepository.existsByCardIdAndUserId(cardId, userId));
        cardDto.setBookmark(cardBookmarkRepository.existsByCardIdAndUserId(cardId, userId));
        cardDto.setEngTranslation(card.getEngTranslation());
        cardDto.setEngPronunciation(card.getEngPronunciation());

        return cardDto;
    }
}
