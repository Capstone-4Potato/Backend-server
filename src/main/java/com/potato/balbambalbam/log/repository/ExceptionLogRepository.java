package com.potato.balbambalbam.log.repository;

import com.potato.balbambalbam.log.entity.ExceptionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExceptionLogRepository extends JpaRepository<ExceptionLog, Long> {
}
