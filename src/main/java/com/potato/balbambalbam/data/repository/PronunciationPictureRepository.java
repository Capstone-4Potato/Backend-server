package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.PronunciationPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PronunciationPictureRepository extends JpaRepository<PronunciationPicture, Long> {

    Optional<PronunciationPicture> findByPhonemeId(Long phonemeId);
}
