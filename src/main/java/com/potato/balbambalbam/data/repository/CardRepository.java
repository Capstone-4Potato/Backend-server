package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Override
    List<Card> findAll();
    List<Card> findAllByCategoryId(Long id);
    List<Card> findByCategoryIdIn(List<Long> categoryIds);

    Optional<Card> findById(Long id);
    List<Card> findByTextOrderById(String text);
}
