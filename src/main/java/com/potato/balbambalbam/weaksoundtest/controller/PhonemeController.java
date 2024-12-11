package com.potato.balbambalbam.weaksoundtest.controller;

import com.potato.balbambalbam.data.entity.Phoneme;
import com.potato.balbambalbam.data.entity.UserWeakSound;
import com.potato.balbambalbam.data.entity.WeakSoundTestStatus;
import com.potato.balbambalbam.data.repository.PhonemeRepository;
import com.potato.balbambalbam.data.repository.UserWeakSoundRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestSatusRepositoy;
import com.potato.balbambalbam.exception.ResponseNotFoundException;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import com.potato.balbambalbam.weaksoundtest.dto.UserWeakSoundDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@Slf4j
public class PhonemeController {

    private final UserWeakSoundRepository userWeakSoundRepository;
    private final PhonemeRepository phonemeRepository;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy;

    public PhonemeController (UserWeakSoundRepository userWeakSoundRepository,
                              PhonemeRepository phonemeRepository,
                              JoinService joinService,
                              JWTUtil jwtUtil,
                              WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy){
        this.userWeakSoundRepository = userWeakSoundRepository;
        this.phonemeRepository = phonemeRepository;
        this.joinService = joinService;
        this.jwtUtil = jwtUtil;
        this.weakSoundTestSatusRepositoy = weakSoundTestSatusRepositoy;
    }

    private Long extractUserIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @GetMapping("/test/status")
    public ResponseEntity<?> getTestStatusByUserId(@RequestHeader("access") String access){
        Long userId = extractUserIdFromToken(access);

        try{
            WeakSoundTestStatus weakSoundTestStatus = weakSoundTestSatusRepositoy.findByUserId(userId);

            if(weakSoundTestStatus == null){
                throw new ResponseNotFoundException("취약음 테스트가 필요합니다.");
            }

            return ResponseEntity.ok(weakSoundTestStatus.getTestStatus());
        } catch (ResponseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("서버 오류가 발생했습니다."); // 500
        }
    }

    @GetMapping("/test/phonemes")
    public ResponseEntity<?> getWeakPhonemesByUserId(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        try {
            List<UserWeakSound> weakPhonemes = userWeakSoundRepository.findAllByUserId(userId);

            if (weakPhonemes == null || weakPhonemes.isEmpty()) {
                throw new ResponseNotFoundException("취약음이 없습니다."); // 404
            }

            List<UserWeakSoundDto> weakPhonemeDtos = IntStream.range(0, weakPhonemes.size())
                    .mapToObj(index -> {
                        UserWeakSound weakPhoneme = weakPhonemes.get(index);
                        Phoneme phoneme = phonemeRepository.findById(weakPhoneme.getUserPhoneme()).orElse(null);
                        String phonemeType = null;
                        String phonemeText = null;
                        if (phoneme != null) {
                            switch (phoneme.getType().intValue()) {
                                case 0:
                                    phonemeType = "Initial consonant";
                                    break;
                                case 1:
                                    phonemeType = "Medial vowel";
                                    break;
                                case 2:
                                    phonemeType = "Final consonant";
                                    break;
                                default:
                                    phonemeType = "?";
                            }
                            phonemeText = phonemeType + " " + phoneme.getText();
                        }
                        return new UserWeakSoundDto(index + 1, phonemeText); // 순위는 1부터 시작
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(weakPhonemeDtos);
        } catch (ResponseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("서버 오류가 발생했습니다."); // 500
        }
    }

    @DeleteMapping("/phonemes")
    public ResponseEntity<?> deleteWeakPhonemesByUserId(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        try {
            List<UserWeakSound> userWeakSounds = userWeakSoundRepository.findAllByUserId(userId);

            if (userWeakSounds != null && !userWeakSounds.isEmpty()) {
                userWeakSoundRepository.deleteAll(userWeakSounds);
            } else {
                throw new ResponseNotFoundException("취약음이 없습니다."); // 404
            }

        } catch (ResponseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("서버 오류가 발생했습니다."); // 500
        } finally {
            WeakSoundTestStatus weakSoundTestStatus = weakSoundTestSatusRepositoy.findByUserId(userId);
            weakSoundTestSatusRepositoy.delete(weakSoundTestStatus);
        }
        return ResponseEntity.ok("사용자의 취약음 데이터가 삭제되었습니다."); //200
    }

    @GetMapping("/phonemes")
    public ResponseEntity<?> listPhonemes() {
        List<Phoneme> phonemes = phonemeRepository.findAll();
        return ResponseEntity.ok(phonemes); //200
    }
}