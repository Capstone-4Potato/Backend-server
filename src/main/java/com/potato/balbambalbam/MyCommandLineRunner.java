package com.potato.balbambalbam;

import com.potato.balbambalbam.main.cardList.service.UpdateEngPronunciationService;
import com.potato.balbambalbam.main.cardList.service.UpdateEngTranslationService;
import com.potato.balbambalbam.main.cardList.service.UpdatePhonemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * SpringApplication.run() 되기 전에 딱 한번 실행되는 class
 * 1. Card 테이블의 phoneme column update
 * 2. Card 테이블의 eng Pronunciation update
 */
@Component
@RequiredArgsConstructor
public class MyCommandLineRunner implements CommandLineRunner {

    private final UpdatePhonemeService updatePhonemeService;
    private final UpdateEngPronunciationService updateEngPronunciationService;

    private final UpdateEngTranslationService updateEngTranslationService;
    @Override
    public void run(String... args) throws Exception {
        updatePhonemeService.updateCardPhonemeColumn();
        updateEngPronunciationService.updateEngPronunciation();
        updateEngTranslationService.updateEngTranslation();
    }
}
