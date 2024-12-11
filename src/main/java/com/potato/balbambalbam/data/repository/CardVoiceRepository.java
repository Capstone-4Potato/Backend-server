package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.CardVoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardVoiceRepository extends JpaRepository<CardVoice, Long> {
    @Override
    Optional<CardVoice> findById(Long aLong);
}