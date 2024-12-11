package com.potato.balbambalbam.weaksoundtest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.weaksoundtest.dto.WeakSoundTestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class WeakSoundTestService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final PhonemeService phonemeService;
    public WeakSoundTestService(WebClient.Builder webClientBuilder,
                                @Value("${ai.service.url}") String aiServiceUrl,
                                ObjectMapper objectMapper,
                                PhonemeService phonemeService){
        this.webClient = webClientBuilder.baseUrl(aiServiceUrl).build();
        this.objectMapper=objectMapper;
        this.phonemeService = phonemeService;
    }

    @Value("${ai.service.url}")
    private String AI_URL;

    public WeakSoundTestDto sendToAi(Long userId, Map<String, Object> dataToSend) throws JsonProcessingException {
        String testRequestJson = objectMapper.writeValueAsString(dataToSend); // dataToSend -> testRequestJson <Json>
        String testResponseJson = webClient.post()
                .uri(AI_URL + "/ai/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testRequestJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        WeakSoundTestDto weakSoundTestDto = objectMapper.readValue(testResponseJson, WeakSoundTestDto.class);
        phonemeService.storePhonemeData(userId, weakSoundTestDto);
        return weakSoundTestDto;
    }
}