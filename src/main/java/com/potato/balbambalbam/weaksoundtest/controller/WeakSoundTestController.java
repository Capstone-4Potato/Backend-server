package com.potato.balbambalbam.weaksoundtest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.data.entity.UserWeakSound;
import com.potato.balbambalbam.data.entity.WeakSoundTest;
import com.potato.balbambalbam.data.entity.WeakSoundTestStatus;
import com.potato.balbambalbam.data.repository.UserWeakSoundRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestSatusRepositoy;
import com.potato.balbambalbam.exception.InvalidParameterException;
import com.potato.balbambalbam.exception.ParameterNotFoundException;
import com.potato.balbambalbam.exception.ResponseNotFoundException;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import com.potato.balbambalbam.weaksoundtest.dto.WeakSoundTestDto;
import com.potato.balbambalbam.weaksoundtest.service.PhonemeService;
import com.potato.balbambalbam.weaksoundtest.service.WeakSoundTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class WeakSoundTestController {

    private final ObjectMapper objectMapper;
    private final WeakSoundTestService weakSoundTestService;
    private final WeakSoundTestRepository weakSoundTestRepository;
    private final PhonemeService phonemeService;
    private final UserWeakSoundRepository userWeakSoundRepository;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy;

    public WeakSoundTestController(ObjectMapper objectMapper,
                                   WeakSoundTestService weakSoundTestService,
                                   WeakSoundTestRepository weakSoundTestRepository,
                                   PhonemeService phonemeService,
                                   UserWeakSoundRepository userWeakSoundRepository,
                                   JoinService joinService,
                                   JWTUtil jwtUtil,
                                   WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy){
        this.objectMapper = objectMapper;
        this.weakSoundTestService = weakSoundTestService;
        this.weakSoundTestRepository = weakSoundTestRepository;
        this.phonemeService = phonemeService;
        this.userWeakSoundRepository = userWeakSoundRepository;
        this.joinService = joinService;
        this.jwtUtil = jwtUtil;
        this.weakSoundTestSatusRepositoy = weakSoundTestSatusRepositoy;
    }

    private Long extractUserIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @GetMapping("/test")
    public ResponseEntity<?> getWeakSoundTest() {
        List<WeakSoundTest> weakSoundTestList = weakSoundTestRepository.findAll();
        return ResponseEntity.ok(weakSoundTestList); //200
    }

    @PostMapping("/test/{cardId}")
    public ResponseEntity<String> uploadFile
            (@PathVariable("cardId") Long id,
             @RequestHeader("access") String access,
             @RequestParam("userAudio")MultipartFile userAudio) throws JsonProcessingException {

        Long userId = extractUserIdFromToken(access);

        if(userAudio.isEmpty()){
            throw new ParameterNotFoundException("사용자 음성 파일이 비었습니다."); //400
        }

        return weakSoundTestRepository.findById(id)
                .map(weakSoundTest -> {
                    try{
                        // userAudio<wav> -> userAudioBytes
                        byte[] userAudioBytes = userAudio.getBytes();
                        // userAudioBytes -> userAudioBase64
                        String userAudioBase64 = Base64.getEncoder().encodeToString(userAudioBytes);

                        Map<String, Object> dataToSend = new HashMap<>();
                        dataToSend.put("userAudio",userAudioBase64);
                        dataToSend.put("correctText",weakSoundTest.getText());

                        WeakSoundTestDto testResponse = weakSoundTestService.sendToAi(userId, dataToSend);
                        String testResponseJson = objectMapper.writeValueAsString(testResponse);

                        log.info("{}",testResponseJson);

                        return ResponseEntity.ok(testResponseJson);
                    } catch (IOException e){
                        throw new RuntimeException("서버 오류가 발생했습니다."); //500
                    }
                })
                .orElseThrow(() -> new InvalidParameterException("해당 id를 가진 테스트 카드가 없습니다.")); //404
    }

    @PostMapping("/test/finalize")
    public ResponseEntity<?> finalizeAnalysis(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        Map<Long, Integer> topPhonemes;

        try {
            topPhonemes = phonemeService.getTopPhonemes(userId);

            if (topPhonemes == null || topPhonemes.isEmpty()) {
                throw new ResponseNotFoundException("취약음이 없습니다."); // 404
            }

            topPhonemes.forEach((phonemeId, count) -> {
                UserWeakSound userWeakSound = new UserWeakSound(userId, phonemeId);
                userWeakSoundRepository.save(userWeakSound);
            });

        } catch (ResponseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("서버 오류가 발생했습니다."); // 500
        } finally {
            WeakSoundTestStatus weakSoundTestStatus = new WeakSoundTestStatus(userId, true);
            weakSoundTestSatusRepositoy.save(weakSoundTestStatus);

            phonemeService.clearTemporaryData(userId);
        }

        return ResponseEntity.ok(topPhonemes); // 200
    }
}