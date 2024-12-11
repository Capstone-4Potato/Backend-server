package com.potato.balbambalbam.main.customCard.service;

import com.potato.balbambalbam.exception.AiGenerationFailException;
import com.potato.balbambalbam.exception.InvalidParameterException;
import com.potato.balbambalbam.main.customCard.dto.AiEngPronunciationRequestDto;
import com.potato.balbambalbam.main.customCard.dto.AiEngPronunciationResponseDto;
import com.potato.balbambalbam.main.customCard.dto.AiKorPronunciationRequestDto;
import com.potato.balbambalbam.main.customCard.dto.AiKorPronunciationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
@Slf4j
@RequiredArgsConstructor
@Service
public class AiPronunciationService {
    WebClient webClient = WebClient.builder().build();
    @Value("${ai.service.url}")
    private String AI_URL;
    public AiKorPronunciationResponseDto getKorPronunciation(String text) {
        AiKorPronunciationRequestDto aiKorPronunciationRequestDto = new AiKorPronunciationRequestDto(text);

        AiKorPronunciationResponseDto responseDto = webClient.post()
                .uri(AI_URL + "/ai/kor-pronunciation")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiKorPronunciationRequestDto), AiKorPronunciationRequestDto.class)
                .retrieve()//요청
                //에러 처리 : 요청이 잘못갔을 경우
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response-> response.bodyToMono(String.class).map(InvalidParameterException::new))
                //에러 처리 : 발음생성실패
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response-> response.bodyToMono(String.class).map(AiGenerationFailException::new))
                .bodyToMono(AiKorPronunciationResponseDto.class)
                .timeout(Duration.ofSeconds(5)) //2초 안에 응답 오지 않으면 TimeoutException 발생
                .block();

        return responseDto;
    }

    public AiEngPronunciationResponseDto getEngPronunciation(String text){
        AiEngPronunciationRequestDto aiEngPronunciationRequest = new AiEngPronunciationRequestDto(text);

        AiEngPronunciationResponseDto responseDto = webClient.post()
                .uri(AI_URL + "/ai/eng-pronunciation")
                .body(Mono.just(aiEngPronunciationRequest), AiEngPronunciationRequestDto.class)
                .retrieve()
                //에러 처리 : 요청이 잘못갔을 경우
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response-> response.bodyToMono(String.class).map(InvalidParameterException::new))
                //에러 처리 : 발음생성실패
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response-> response.bodyToMono(String.class).map(AiGenerationFailException::new))
                .bodyToMono(AiEngPronunciationResponseDto.class)
                .timeout(Duration.ofSeconds(5)) //2초 안에 응답 오지 않으면 TimeoutException 발생
                .block();

        return responseDto;
    }
}