package com.potato.balbambalbam.log.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "exception_info")
public class ExceptionInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exception_info_id")
    private Long exceptionInfoId;

    @Column(name = "exception_name")
    private String exceptionName;

    @Column(name = "exception_httpstatus")
    private int exceptionHttpStatus;

    @Column(name = "exception_message")
    private String exceptionMessage;

    @Column(name = "exception_level")
    private String exceptionLevel;
}