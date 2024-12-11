package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.UserWeakSound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWeakSoundRepository extends JpaRepository<UserWeakSound, Long> {

    List<UserWeakSound> findAllByUserId(Long userId);
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
