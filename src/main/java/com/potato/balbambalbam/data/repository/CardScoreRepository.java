package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.CardScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardScoreRepository extends JpaRepository<CardScore, Long> {

    Optional<CardScore> findByCardIdAndUserId(Long cardId, Long userId);
    List<CardScore> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
