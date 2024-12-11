package com.potato.balbambalbam.main.cardFeedback.service;

import com.potato.balbambalbam.exception.AiGenerationFailException;
import com.potato.balbambalbam.exception.InvalidParameterException;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class AiCardFeedbackService {
    WebClient webClient = WebClient.builder().build();
    @Value("${ai.service.url}")
    private String AI_URL;
    public AiFeedbackResponseDto postAiFeedback(AiFeedbackRequestDto aiFeedbackRequestDto)  {

        AiFeedbackResponseDto aiFeedbackResponseDto = webClient.post()
                .uri(AI_URL + "/ai/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiFeedbackRequestDto), AiFeedbackRequestDto.class)
                .retrieve()//요청
                //에러 처리 : 요청이 잘못갔을 경우
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response-> response.bodyToMono(String.class).map(InvalidParameterException::new))
                //에러 처리 : 사용자 텍스트 추출 실패
                .onStatus(HttpStatus.UNPROCESSABLE_ENTITY::equals,
                        response-> response.bodyToMono(String.class).map(AiGenerationFailException::new))
                //에러 처리 : 텍스트 분리 실패, 정확도 계산 실패, waveform 실패
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response-> response.bodyToMono(String.class).map(AiGenerationFailException::new))
                .bodyToMono(AiFeedbackResponseDto.class)
                .timeout(Duration.ofSeconds(10)) //10초 안에 응답 오지 않으면 TimeoutException 발생
                .block();


        return aiFeedbackResponseDto;
    }
}
