package com.potato.balbambalbam.main.cardInfo.service;

import com.potato.balbambalbam.exception.AiGenerationFailException;
import com.potato.balbambalbam.exception.InvalidParameterException;
import com.potato.balbambalbam.main.cardInfo.dto.AiTtsRequestDto;
import com.potato.balbambalbam.main.cardInfo.dto.AiTtsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 인공지능서버와 통신하여 생성된 음성을 받아옴
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiTtsService {
    WebClient webClient = WebClient.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1 * 1024 * 1024)).build();
    @Value("${ai.service.url}")
    private String AI_URL;
    public AiTtsResponseDto getTtsVoice(AiTtsRequestDto aiTtsRequestDto) {

        AiTtsResponseDto aiTtsResponseDto = webClient.post()
                .uri(AI_URL + "/ai/voice")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiTtsRequestDto), AiTtsRequestDto.class)
                .retrieve()//요청
                //에러 처리 : 요청이 잘못갔을 경우
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response-> response.bodyToMono(String.class).map(InvalidParameterException::new))
                //에러 처리 : 음성 생성이 실패한 경우
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response-> response.bodyToMono(String.class).map(AiGenerationFailException::new))
                //성공
                .bodyToMono(AiTtsResponseDto.class)
                //에러 처리 : 5초 안에 응답 오지 않으면 TimeoutException 발생
                .timeout(Duration.ofSeconds(5))
                .block();

        return aiTtsResponseDto;
    }
}
