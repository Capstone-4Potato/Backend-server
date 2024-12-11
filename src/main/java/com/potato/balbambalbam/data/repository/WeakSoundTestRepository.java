package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.WeakSoundTest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeakSoundTestRepository extends JpaRepository<WeakSoundTest, Long> {
    List<WeakSoundTest> findByIdGreaterThanOrderByIdAsc(Long id);
}
