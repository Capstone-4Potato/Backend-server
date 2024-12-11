package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.CardWeakSound;
import com.potato.balbambalbam.data.entity.CardWeakSoundId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardWeakSoundRepository extends JpaRepository<CardWeakSound, CardWeakSoundId> {

    boolean existsByCardIdAndUserId(Long cardId, Long userId);
    Optional<CardWeakSound> findByCardId(Long cardId);
    @Transactional
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
