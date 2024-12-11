package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.Phoneme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhonemeRepository extends JpaRepository<Phoneme, Long> {

    Optional<Phoneme> findPhonemeByTypeAndText(Long type, String text);
    List<Phoneme> findPhonemeByTextOrderById(String text);
    @Query("select p from phoneme p where p.type in :types and p.text = :text")
    List<Phoneme> findByTypeAndText(@Param("types") List<Long> types, @Param("text") String text);

}
