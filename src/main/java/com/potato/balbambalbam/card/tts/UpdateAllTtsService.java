package com.potato.balbambalbam.card.tts;

import com.potato.balbambalbam.card.tts.dto.AiAllTtsResponseDto;
import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardVoice;
import com.potato.balbambalbam.data.entity.TodayCard;
import com.potato.balbambalbam.data.repository.CardVoiceRepository;
import com.potato.balbambalbam.data.repository.TodayCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateAllTtsService {
    private final AiAllTtsService aiAllTtsService;
    private final TodayCardRepository todayCardRepository;
    private final CardVoiceRepository cardVoiceRepository;

    public void updateCardVoice(Card card) {
        saveSixVoices(card.getCardId(), aiAllTtsService.getAiTtsResponse(card.getText()));
    }

    public void updateTodayCardVoice(TodayCard card) {
        saveTodayCardSixVoices(card, aiAllTtsService.getAiTtsResponse(card.getText()));
    }

    @Transactional
    protected void saveTodayCardSixVoices(TodayCard card, AiAllTtsResponseDto aiTtsResponse) {
        card.saveCardVoice(aiTtsResponse.child_0, aiTtsResponse.child_1, aiTtsResponse.adult_0, aiTtsResponse.adult_1, aiTtsResponse.elderly_0, aiTtsResponse.elderly_1);
        todayCardRepository.save(card);
    }

    @Transactional
    protected void saveSixVoices(Long id, AiAllTtsResponseDto allTtsResponseDto) {

        CardVoice cardvoice = CardVoice.builder()
                .id(id)
                .childMale(allTtsResponseDto.getChild_0())
                .childFemale(allTtsResponseDto.getChild_1())
                .adultMale(allTtsResponseDto.getAdult_0())
                .adultFemale(allTtsResponseDto.getAdult_1())
                .elderlyMale(allTtsResponseDto.getElderly_0())
                .elderlyFemale(allTtsResponseDto.getElderly_1())
                .build();

        cardVoiceRepository.save(cardvoice);
    }
}
