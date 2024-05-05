package com.potato.balbambalbam.main.cardInfo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.main.MyConstant;
import com.potato.balbambalbam.main.cardInfo.dto.AiTtsRequestDto;
import com.potato.balbambalbam.main.cardInfo.dto.AiTtsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    WebClient webClient = WebClient.builder().build();
    ObjectMapper objectMapper = new ObjectMapper();
    public AiTtsResponseDto getTtsVoice(AiTtsRequestDto aiTtsRequestDto) throws JsonProcessingException {

        AiTtsResponseDto aiTtsResponseDto = webClient.post()
                .uri(MyConstant.AI_URL + "/ai/voice")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiTtsRequestDto), AiTtsRequestDto.class)
                .retrieve()//요청
                .bodyToMono(AiTtsResponseDto.class)
                .timeout(Duration.ofSeconds(5)) //5초 안에 응답 오지 않으면 TimeoutException 발생
                .block();

        return aiTtsResponseDto;
    }
}