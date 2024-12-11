package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.Level;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level, Long> {
    Optional<Level> findByLevelId(Long levelId);
}
