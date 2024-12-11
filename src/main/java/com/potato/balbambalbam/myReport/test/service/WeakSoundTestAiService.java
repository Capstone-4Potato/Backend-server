package com.potato.balbambalbam.myReport.test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.exception.AiGenerationFailException;
import com.potato.balbambalbam.exception.InvalidParameterException;
import com.potato.balbambalbam.myReport.test.dto.TestResponseDto;
import com.potato.balbambalbam.myReport.weaksound.service.PhonemeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class WeakSoundTestAiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final PhonemeService phonemeService;
    public WeakSoundTestAiService(WebClient.Builder webClientBuilder,
                                  @Value("${ai.service.url}") String aiServiceUrl,
                                  ObjectMapper objectMapper,
                                  PhonemeService phonemeService){
        this.webClient = webClientBuilder.baseUrl(aiServiceUrl).build();
        this.objectMapper=objectMapper;
        this.phonemeService = phonemeService;
    }

    @Value("${ai.service.url}")
    private String AI_URL;

    public TestResponseDto sendToAi(Long userId, Map<String, Object> dataToSend) throws JsonProcessingException {
        String testRequestJson = objectMapper.writeValueAsString(dataToSend); // dataToSend -> testRequestJson <Json>
        String testResponseJson = webClient.post()
                .uri(AI_URL + "/ai/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testRequestJson)
                .retrieve()
                //에러 처리 : 요청이 잘못갔을 경우
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class).map(InvalidParameterException::new))
                //에러 처리 : 요청이 잘못갔을 경우
                .onStatus(HttpStatus.UNPROCESSABLE_ENTITY::equals,
                        response -> response.bodyToMono(String.class).map(InvalidParameterException::new))
                //에러 처리 : 응답 생성 실패
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response -> response.bodyToMono(String.class).map(AiGenerationFailException::new))
                .bodyToMono(String.class)
                .block();
        TestResponseDto weakSoundTestDto = objectMapper.readValue(testResponseJson, TestResponseDto.class);
        phonemeService.storePhonemeData(userId, weakSoundTestDto);
        return weakSoundTestDto;
    }
}