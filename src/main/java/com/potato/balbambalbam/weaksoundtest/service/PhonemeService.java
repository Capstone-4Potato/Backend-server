package com.potato.balbambalbam.weaksoundtest.service;

import com.potato.balbambalbam.data.entity.Phoneme;
import com.potato.balbambalbam.data.repository.PhonemeRepository;
import com.potato.balbambalbam.weaksoundtest.dto.WeakSoundTestDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PhonemeService {

    private final PhonemeRepository phonemeRepository;
    private Map<Long, Map<Long, Integer>> temporaryStorage = new HashMap<>();

    public PhonemeService (PhonemeRepository phonemeRepository){
        this.phonemeRepository = phonemeRepository;
    }

    @Transactional
    public void storePhonemeData(Long userId, WeakSoundTestDto dto) { //임시 저장소
        Map<Long, Integer> phonemeCounts = temporaryStorage.getOrDefault(userId, new HashMap<>());
        Map<Long, Integer> newPhonemeCounts = new HashMap<>();

        // UserWeakPhoneme -> type(0,1), UserWeakPhonemeLast -> type(2)
        phonemeMap(dto.getUserWeakPhoneme(), Arrays.asList(0L, 1L), newPhonemeCounts);
        phonemeMap(dto.getUserWeakPhonemeLast(), Collections.singletonList(2L), newPhonemeCounts);

        newPhonemeCounts.forEach((key, value) -> phonemeCounts.merge(key, value, Integer::sum));
        temporaryStorage.put(userId, phonemeCounts);
    }

    private void phonemeMap(Map<String, Integer> testResponseMap, List<Long> types, Map<Long, Integer> newPhonemeCounts) {
        for (Map.Entry<String, Integer> entry : testResponseMap.entrySet()) {
            List<Phoneme> phonemes = phonemeRepository.findByTypeAndText(types, entry.getKey());
            phonemes.forEach(phoneme -> newPhonemeCounts.merge(phoneme.getId(), entry.getValue(), Integer::sum));
        }
    }

    public Map<Long, Integer> getTopPhonemes(Long userId) {
        return temporaryStorage.getOrDefault(userId, new HashMap<>())
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(4)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void clearTemporaryData(Long userId) {
        temporaryStorage.remove(userId);
    }
}
