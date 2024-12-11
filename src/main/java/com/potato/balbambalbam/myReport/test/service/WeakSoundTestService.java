package com.potato.balbambalbam.myReport.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.data.entity.UserWeakSound;
import com.potato.balbambalbam.data.entity.WeakSoundTest;
import com.potato.balbambalbam.data.entity.WeakSoundTestStatus;
import com.potato.balbambalbam.data.repository.UserWeakSoundRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestSatusRepositoy;
import com.potato.balbambalbam.exception.ResponseNotFoundException;
import com.potato.balbambalbam.myReport.test.dto.TestResponseDto;
import com.potato.balbambalbam.myReport.test.dto.TestStartResponseDto;
import com.potato.balbambalbam.myReport.test.dto.WeakSoundTestListDto;
import com.potato.balbambalbam.myReport.weaksound.service.PhonemeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeakSoundTestService {
    private final WeakSoundTestRepository weakSoundTestRepository;
    private final ObjectMapper objectMapper;
    private final WeakSoundTestAiService weakSoundTestAiService;
    private final PhonemeService phonemeService;
    private final UserWeakSoundRepository userWeakSoundRepository;
    private final WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy;
    private final Map<Long, Long> lastCardProgress = new ConcurrentHashMap<>();

    public TestStartResponseDto checkTestStatus(Long userId) {
        return new TestStartResponseDto(phonemeService.hasTemporaryData(userId));
    }

    public void startNewTest(Long userId) {
        phonemeService.clearTemporaryData(userId);
        lastCardProgress.remove(userId);
    }

    public List<WeakSoundTestListDto> getAllTests() {
        return weakSoundTestRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<WeakSoundTestListDto> getContinueTests(Long userId) {
        Long lastCardId = lastCardProgress.get(userId);
        if (lastCardId == null) {
            throw new ResponseNotFoundException("진행중인 테스트를 찾을 수 없습니다.");
        }
        return weakSoundTestRepository.findByIdGreaterThanOrderByIdAsc(lastCardId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private WeakSoundTestListDto convertToDto(WeakSoundTest test) {
        return new WeakSoundTestListDto(
                test.getId(),
                test.getText(),
                test.getPronunciation(),
                test.getEngPronunciation(),
                test.getEngTranslation());
    }

    public String processUserAudio(MultipartFile userAudio, WeakSoundTest weakSoundTest, Long userId) throws IOException {
        lastCardProgress.put(userId, weakSoundTest.getId());

        byte[] userAudioBytes = userAudio.getBytes();
        String userAudioBase64 = Base64.getEncoder().encodeToString(userAudioBytes);

        Map<String, Object> dataToSend = new HashMap<>();
        dataToSend.put("userAudio", userAudioBase64);
        dataToSend.put("correctText", weakSoundTest.getText());

        TestResponseDto testResponse = weakSoundTestAiService.sendToAi(userId, dataToSend);
        return objectMapper.writeValueAsString(testResponse);
    }

    @Transactional
    public Map<Long, Integer> getTopPhonemes(Long userId) {
        Map<Long, Integer> topPhonemes = phonemeService.getTopPhonemes(userId);
        if (topPhonemes == null || topPhonemes.isEmpty()) {
            throw new ResponseNotFoundException("취약음소가 없습니다.");
        }
        return topPhonemes;
    }

    @Transactional
    public void finalizeTestStatus(Long userId) {
        try {
            deleteExistingWeakSoundData(userId);
            Map<Long, Integer> topPhonemes = phonemeService.getTopPhonemes(userId);
            topPhonemes.forEach((phonemeId, count) -> {
                UserWeakSound userWeakSound = new UserWeakSound(userId, phonemeId);
                userWeakSoundRepository.save(userWeakSound);
            });
            WeakSoundTestStatus weakSoundTestStatus = new WeakSoundTestStatus(userId, true);
            weakSoundTestSatusRepositoy.save(weakSoundTestStatus);
        } finally {
            phonemeService.clearTemporaryData(userId);
            lastCardProgress.remove(userId);
        }
    }

    private void deleteExistingWeakSoundData(Long userId) {
        List<UserWeakSound> userWeakSounds = userWeakSoundRepository.findAllByUserId(userId);
        if (!userWeakSounds.isEmpty()) {
            userWeakSoundRepository.deleteAll(userWeakSounds);
        }
        WeakSoundTestStatus testStatus = weakSoundTestSatusRepositoy.findByUserId(userId);
        if (testStatus != null) {
            weakSoundTestSatusRepositoy.delete(testStatus);
        }
    }
}