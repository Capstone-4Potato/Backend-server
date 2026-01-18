package com.potato.balbambalbam.card.cardFeedback.service;

import com.potato.balbambalbam.card.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.card.cardFeedback.dto.AiFeedbackResponseDto;
import com.potato.balbambalbam.exception.AiGenerationFailException;
import com.potato.balbambalbam.exception.AiServerException;
import com.potato.balbambalbam.exception.InvalidParameterException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiCardFeedbackService {

    private final WebClient aiWebClient;

    private final Retry retryPolicy = Retry.backoff(2, Duration.ofMillis(200))
            .maxBackoff(Duration.ofSeconds(1))
            .jitter(0.3)
            .filter(this::isRetryable);

    @CircuitBreaker(name = "aiFeedback", fallbackMethod = "fallback")
    public AiFeedbackResponseDto postAiFeedback(AiFeedbackRequestDto aiFeedbackRequestDto) {
        return aiWebClient.post()
                .uri("/ai/feedback")
                .bodyValue(aiFeedbackRequestDto)
                .retrieve()//요청
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        this::mapError)
                .bodyToMono(AiFeedbackResponseDto.class)
                .retryWhen(retryPolicy)
                .timeout(Duration.ofSeconds(15)) // 전체 시도(Retry 포함)에 대한 마지노선
                .block();
    }

    private Mono<? extends Throwable> mapError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .map(body -> {
                    int statusCode = response.statusCode().value();
                    if (statusCode == 400) return new InvalidParameterException(body);
                    if (statusCode == 422) return new AiGenerationFailException(body);
                    if (statusCode >= 500) return new AiServerException(body);
                    return new RuntimeException("AI error: " + statusCode + " body=" + body);
                });
    }

    private boolean isRetryable(Throwable ex) {
        return ex instanceof TimeoutException
                || ex instanceof WebClientRequestException
                || ex instanceof AiServerException
                || ex.getCause() instanceof io.netty.handler.timeout.ReadTimeoutException;
    }

    private AiFeedbackResponseDto fallback(Throwable t) {
        log.warn("AI circuit breaker OPEN - fallback executed", t);
        throw new AiServerException("AI service temporarily unavailable");
    }

}
