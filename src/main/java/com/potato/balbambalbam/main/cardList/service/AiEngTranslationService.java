package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.exception.AiGenerationFailException;
import com.potato.balbambalbam.exception.InvalidParameterException;
import com.potato.balbambalbam.main.cardList.dto.EngTranslationRequestDto;
import com.potato.balbambalbam.main.cardList.dto.EngTranslationResponseDto;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AiEngTranslationService {
    WebClient webClient = WebClient.builder().build();
    @Value("${ai.service.url}")
    private String AI_URL;
    public EngTranslationResponseDto getEngTranslation(String text) {
        EngTranslationRequestDto engTranslationRequestDto = new EngTranslationRequestDto(text);

        EngTranslationResponseDto responseDto = webClient.post()
                .uri(AI_URL + "/ai/eng-translation")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(engTranslationRequestDto), EngTranslationRequestDto.class)
                .retrieve()//요청
                //에러 처리 : 요청이 잘못갔을 경우
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response-> response.bodyToMono(String.class).map(InvalidParameterException::new))
                //에러 처리 : 응답 생성 실패
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response-> response.bodyToMono(String.class).map(AiGenerationFailException::new))
                .bodyToMono(EngTranslationResponseDto.class)
                .timeout(Duration.ofSeconds(5)) //2초 안에 응답 오지 않으면 TimeoutException 발생
                .block();

        return responseDto;
    }

}
