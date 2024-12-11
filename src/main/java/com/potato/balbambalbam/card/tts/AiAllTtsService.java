package com.potato.balbambalbam.card.tts;

import com.potato.balbambalbam.card.tts.dto.AiAllTtsRequestDto;
import com.potato.balbambalbam.card.tts.dto.AiAllTtsResponseDto;
import com.potato.balbambalbam.exception.AiGenerationFailException;
import com.potato.balbambalbam.exception.InvalidParameterException;
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
public class AiAllTtsService {
    WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(32 * 1024 * 1024))
            .build();
    @Value("${ai.service.url}")
    private String AI_URL;

    public AiAllTtsResponseDto getAiTtsResponse(String text) {
        AiAllTtsRequestDto aiAllTtsRequestDto = new AiAllTtsRequestDto(text);

        return webClient.post()
                .uri(AI_URL + "/ai/db-voice")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiAllTtsRequestDto), AiAllTtsRequestDto.class)
                .retrieve()//요청
                //에러 처리 : 요청이 잘못갔을 경우
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class).map(InvalidParameterException::new))
                //에러 처리 : 발음생성실패
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response -> response.bodyToMono(String.class).map(AiGenerationFailException::new))
                .bodyToMono(AiAllTtsResponseDto.class)
                .timeout(Duration.ofSeconds(10)) //10초 안에 응답 오지 않으면 TimeoutException 발생
                .block();
    }
}
