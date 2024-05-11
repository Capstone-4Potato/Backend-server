package com.potato.balbambalbam.main;

import com.potato.balbambalbam.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class MainExceptionResolverController extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        String className = extractClassName(ex.getClass().toString());

        ExceptionDto exceptionDto = new ExceptionDto(statusCode.value(), className, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, headers, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String className = extractClassName(ex.getClass().toString());
        String defaultMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        ExceptionDto exceptionDto = new ExceptionDto(status.value(), className, defaultMessage);
        return new ResponseEntity<>(exceptionDto, headers, status);
    }

    @ExceptionHandler({AiConnectionException.class, UserNotFoundException.class, CategoryNotFoundException.class, CardNotFoundException.class})
    public ResponseEntity<ExceptionDto> notFoundExceptionHandler(Exception ex){
        return exceptionHandler(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({TimeoutException.class, CardDeleteException.class, WebClientResponseException.InternalServerError.class, RuntimeException.class})
    public ResponseEntity<ExceptionDto> timeoutExceptionHandler(Exception ex){
        return exceptionHandler(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 에러 메서드 처리
     * @param ex
     * @param httpStatus
     * @return
     */
    protected ResponseEntity<ExceptionDto> exceptionHandler(Exception ex, HttpStatusCode httpStatus){
        String className = extractClassName(ex.getClass().toString());
        String exMessage = ex.getMessage();

        log.info("[ERROR] ["+ className + "]:" + exMessage);

        return ResponseEntity.status(httpStatus).body(new ExceptionDto(httpStatus.value(), className, exMessage));
    }

    protected String extractClassName(String fullClassName){
        String[] classNameParts = fullClassName.split("\\.");
        return classNameParts[classNameParts.length - 1];
    }
}
