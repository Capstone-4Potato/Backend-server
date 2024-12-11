package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.CustomCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomCardRepository extends JpaRepository<CustomCard, Long> {
    List<CustomCard> findAllByUserId(Long userId);

    @Override
    Optional<CustomCard> findById(Long cardId);

    Optional<CustomCard> findCustomCardByIdAndUserId(Long cardId, Long userId);

    @Override
    boolean existsById(Long aLong);

    void deleteUserById(Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT COUNT(cc) FROM custom_card cc WHERE cc.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}