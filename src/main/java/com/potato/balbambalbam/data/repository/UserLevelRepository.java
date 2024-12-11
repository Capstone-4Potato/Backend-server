package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {

    Optional<UserLevel> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);
}
