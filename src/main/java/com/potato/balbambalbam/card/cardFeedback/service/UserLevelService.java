package com.potato.balbambalbam.card.cardFeedback.service;

import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.entity.Level;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLevelService {
    private final UserLevelRepository userLevelRepository;
    private final LevelRepository levelRepository;
    private final CustomCardRepository customCardRepository;
    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;

    @Transactional
    public void updateUserLevelInfo(Long cardId, Long userId) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        //경험치 update
        updateUserExperience(cardId, userLevel);
        //레벨 체크
        updateUserLevel(userLevel);
    }

    @Transactional
    public void updateCustomCardUserLevelInfo(Long cardId, Long userId) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        //경험치 update
        updateUserCustomExperience(cardId, userLevel);
        //레벨 체크
        updateUserLevel(userLevel);
    }


    protected void updateUserLevel(UserLevel userLevel) {
        Level currentLevel = levelRepository.findByLevelId(userLevel.getLevelId()).get();
        if(userLevel.getLevelId() == 25) return;
        if(currentLevel.getLevelExperience() <= userLevel.getUserExperience()) {
            userLevel.setLevelId(userLevel.getLevelId() + 1);
            userLevel.setUserExperience(0L);
            userLevelRepository.save(userLevel);
        }
    }

    protected void updateUserExperience(Long cardId, UserLevel userLevel) {
        Long categoryId = cardRepository.findByCardId(cardId).orElseThrow(() -> new CardNotFoundException("존재하지 않는 카드입니다.")).getCategoryId();

        if(hasMaxScore(cardId, userLevel)) {
            return;
        }

        int experience;
        if (categoryId < 5) experience = 1;
        else if (categoryId < 15) experience = 5;
        else experience = 10;
        userLevel.setUserExperience(userLevel.getUserExperience() + experience);

        userLevelRepository.saveAndFlush(userLevel);
    }

    private boolean hasMaxScore(Long cardId, UserLevel userLevel) {
        if(cardScoreRepository.findById(cardId).isPresent()) {
            if(cardScoreRepository.findById(cardId).get().getHighestScore() == 100) return true;
        }

        return false;
    }

    @Transactional
    protected void updateUserCustomExperience(Long cardId, UserLevel userLevel) {
        if(hasCustomCardMaxScore(cardId, userLevel)) {
            return;
        }

        int experience = 10;
        userLevel.setUserExperience(userLevel.getUserExperience() + experience);
        userLevelRepository.save(userLevel);
    }

    private boolean hasCustomCardMaxScore(Long cardId, UserLevel userLevel) {
        if(customCardRepository.findById(cardId).isPresent()) {
            if(customCardRepository.findById(cardId).get().getHighestScore() == 100) return true;
        }

        return false;
    }
}
