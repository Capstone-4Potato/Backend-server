package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.CardWeakSound;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BulkRepository {
    private final JdbcTemplate jdbcTemplate;
    @PersistenceContext
    private EntityManager em;

    public void saveAll(List<CardWeakSound> cardWeakSoundList){
        String sql = "INSERT INTO card_weaksound (user_id, card_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql,
                cardWeakSoundList,
                cardWeakSoundList.size(),
                (PreparedStatement ps, CardWeakSound cardWeakSound) -> {
                    ps.setLong(1, cardWeakSound.getUserId());
                    ps.setLong(2, cardWeakSound.getCardId());
                });

        em.clear();
    }

    public void deleteAllByUserId(Long userId){
        String sql = "DELETE FROM card_weaksound WHERE user_id = ?";

        jdbcTemplate.update(sql, userId);
    }
}
