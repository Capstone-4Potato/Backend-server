package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.CardBookmark;
import com.potato.balbambalbam.data.entity.CardBookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBookmarkRepository extends JpaRepository<CardBookmark, CardBookmarkId> {

    boolean existsByCardIdAndUserId(Long cardId, Long userId);
    void deleteByCardIdAndUserId(Long cardId, Long userId);
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
