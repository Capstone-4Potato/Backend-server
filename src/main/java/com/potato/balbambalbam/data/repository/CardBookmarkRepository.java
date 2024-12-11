package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.CardBookmark;
import com.potato.balbambalbam.data.entity.CardBookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardBookmarkRepository extends JpaRepository<CardBookmark, CardBookmarkId> {
    boolean existsByCardIdAndUserId(Long cardId, Long userId);

    void deleteByCardIdAndUserId(Long cardId, Long userId);

    void deleteByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<CardBookmark> findAllByUserId(Long userId);

    @Query("SELECT COUNT(cb) FROM card_bookmark cb WHERE cb.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}