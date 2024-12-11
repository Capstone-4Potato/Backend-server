package com.potato.balbambalbam.exception.controller;

import com.potato.balbambalbam.exception.*;
import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.log.service.ExceptionLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class MainExceptionResolverController extends ResponseEntityExceptionHandler {

    private final ExceptionLogService exceptionLogService;

    @Autowired
    public MainExceptionResolverController(ExceptionLogService exceptionLogService) {
        this.exceptionLogService = exceptionLogService;
    }

    // Spring MVC에서 발생하는 일반적인 예외 처리 -> Error
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        exceptionLogService.logException(ex, HttpStatus.valueOf(statusCode.value()), request);
        String className = ex.getStackTrace().length > 0 ? ex.getStackTrace()[0].getClassName() : "Unknown";
        String exceptionName = extractClassName(ex.getClass().toString());
        ExceptionDto exceptionDto = new ExceptionDto(statusCode.value(), exceptionName, ex.getMessage(), className);
        log.error("[{}]: {}", exceptionName, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, headers, statusCode);
    }

    // 유효성 예외 처리 -> Warn
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        exceptionLogService.logException(ex, HttpStatus.valueOf(status.value()), request);
        String className = extractClassName(ex.getClass().toString());
        String defaultMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ExceptionDto exceptionDto = new ExceptionDto(status.value(), className, defaultMessage);
        log.warn("[{}]: {}", className, defaultMessage);
        return new ResponseEntity<>(exceptionDto, headers, status);
    }

    // NOT_FOUND(404)
    @ExceptionHandler({UserNotFoundException.class, CategoryNotFoundException.class,
            CardNotFoundException.class, ResponseNotFoundException.class, CardGenerationFailException.class})
    public ResponseEntity<ExceptionDto> notFoundExceptionHandler(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        if (!isExcludedFromLogging(ex)) {
            exceptionLogService.logInfoException(ex, status, request);
        }
        return exceptionHandler(ex, status);
    }

    // INTERNAL_SERVER-ERROR(500)
    @ExceptionHandler({AiConnectionException.class, CardDeleteException.class,
            AiGenerationFailException.class, VoiceNotFoundException.class})
    public ResponseEntity<ExceptionDto> internalServerErrorExceptionHandler(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (!isExcludedFromLogging(ex)) {
            exceptionLogService.logInfoException(ex, status, request);
        }
        return exceptionHandler(ex, status);
    }

    // BAD_REQUEST(400)
    @ExceptionHandler({InvalidUserNameException.class, ParameterNotFoundException.class})
    public ResponseEntity<ExceptionDto> badRequestExceptionHandler(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (!isExcludedFromLogging(ex)) {
            exceptionLogService.logInfoException(ex, status, request);
        }
        return exceptionHandler(ex, status);
    }

    // UNAUTHORIZED(401)
    @ExceptionHandler({TokenExpiredException.class})
    public ResponseEntity<ExceptionDto> unauthorizedExceptionHandler(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        if (!isExcludedFromLogging(ex)) {
            exceptionLogService.logException(ex, status, request);
        }
        return exceptionHandler(ex, status);
    }

    // 공통 예외 처리
    protected ResponseEntity<ExceptionDto> exceptionHandler(Exception ex, HttpStatusCode httpStatus) {
        String className = extractClassName(ex.getClass().toString());
        String exMessage = ex.getMessage();
        return ResponseEntity.status(httpStatus).body(new ExceptionDto(httpStatus.value(), className, exMessage));
    }

    // 처리되지 않은 예외 발생 ->  Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> globalExceptionHandler(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        exceptionLogService.logException(ex, status, request);
        String className = extractClassName(ex.getClass().toString());
        String exMessage = ex.getMessage();
        log.error("[{}]: {}", className, exMessage);
        return ResponseEntity.status(status).body(new ExceptionDto(status.value(), className, exMessage));
    }

    //클래스 이름 추출
    protected String extractClassName(String fullClassName) {
        String[] classNameParts = fullClassName.split("\\.");
        return classNameParts[classNameParts.length - 1];
    }

    // 자주 발생하는 예외 로그 제외
    private boolean isExcludedFromLogging(Exception ex) {
        return (ex instanceof VoiceNotFoundException ||
                ex instanceof ParameterNotFoundException ||
                ex instanceof TokenExpiredException);
    }
}