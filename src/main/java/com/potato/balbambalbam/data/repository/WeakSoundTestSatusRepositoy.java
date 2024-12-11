package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.WeakSoundTestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeakSoundTestSatusRepositoy extends JpaRepository<WeakSoundTestStatus,Long> {

    WeakSoundTestStatus findByUserId(Long userId);
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
