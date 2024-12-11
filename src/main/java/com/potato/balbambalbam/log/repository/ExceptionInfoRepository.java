package com.potato.balbambalbam.log.repository;

import com.potato.balbambalbam.log.entity.ExceptionInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExceptionInfoRepository extends JpaRepository<ExceptionInfo, Long> {
    Optional<ExceptionInfo> findByExceptionNameAndExceptionMessageAndExceptionHttpStatus(
            String exceptionName, String exceptionMessage, int exceptionHttpStatus);
}
