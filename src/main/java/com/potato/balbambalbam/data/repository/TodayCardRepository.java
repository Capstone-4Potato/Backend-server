package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.TodayCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodayCardRepository extends JpaRepository<TodayCard, Long> {
    @Override
    Optional<TodayCard> findById(Long aLong);

}
