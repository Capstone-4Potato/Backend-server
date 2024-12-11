package com.potato.balbambalbam.main.cardFeedback.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.entity.Category;
import com.potato.balbambalbam.data.entity.Phoneme;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardScoreRepository;
import com.potato.balbambalbam.data.repository.CategoryRepository;
import com.potato.balbambalbam.data.repository.PhonemeRepository;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackResponseDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackResponseDto;
import com.potato.balbambalbam.main.cardList.service.UpdatePhonemeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CardFeedbackService {

    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;
    private final AiCardFeedbackService aiCardFeedbackService;
    private final PhonemeRepository phonemeRepository;
    private final UpdatePhonemeService updatePhonemeService;
    private final CategoryRepository categoryRepository;

    public UserFeedbackResponseDto postUserFeedback(UserFeedbackRequestDto userFeedbackRequestDto, Long userId, Long cardId) {
        Long categoryId = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getCategoryId();
        //인공지능서버와 통신
        AiFeedbackResponseDto aiFeedbackResponseDto = getAiFeedbackResponseDto(userFeedbackRequestDto, cardId);

        //점수 업데이트
        if(categoryId >= 15) {
            updateScoreIfLarger(userId, cardId, aiFeedbackResponseDto.getUserAccuracy());
        }


        //학습카드 추천
        Map<Long, UserFeedbackResponseDto.RecommendCardInfo> recommendCard = createRecommendCard(aiFeedbackResponseDto, categoryId);

        return setUserFeedbackResponseDto(cardId, aiFeedbackResponseDto, recommendCard);
    }

    /**
     * Ai 통신
     * @param aiFeedbackRequest
     * @param cardId
     * @return
     * @throws JsonProcessingException
     */
    protected AiFeedbackResponseDto getAiFeedbackResponseDto (UserFeedbackRequestDto aiFeedbackRequest, Long cardId) {
        AiFeedbackRequestDto aiFeedbackRequestDto = createAiFeedbackRequestDto(aiFeedbackRequest, cardId);
        AiFeedbackResponseDto aiFeedbackResponseDto = aiCardFeedbackService.postAiFeedback(aiFeedbackRequestDto);

        return aiFeedbackResponseDto;
    }

    protected AiFeedbackRequestDto createAiFeedbackRequestDto (UserFeedbackRequestDto userFeedbackRequestDto, Long cardId){
        String pronunciation = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getText();
        AiFeedbackRequestDto aiFeedbackRequestDto = new AiFeedbackRequestDto();

        aiFeedbackRequestDto.setUserAudio(userFeedbackRequestDto.getUserAudio());
        aiFeedbackRequestDto.setCorrectAudio(userFeedbackRequestDto.getCorrectAudio());
        aiFeedbackRequestDto.setPronunciation(pronunciation);

        return aiFeedbackRequestDto;
    }

    /**
     * 사용자 최고 점수 업데이트
     * @param userId
     * @param cardId
     * @param userScore
     */
    public void updateScoreIfLarger(Long userId, Long cardId, Integer userScore){
         Optional<CardScore> optionalCardScore = cardScoreRepository.findByCardIdAndUserId(cardId, userId);

        if(optionalCardScore.isPresent()){
            CardScore cardScore = optionalCardScore.get();
            if(cardScore.getHighestScore() < userScore){
                cardScore.setHighestScore(userScore);
                cardScoreRepository.save(cardScore);
            }
        }else{
            cardScoreRepository.save(new CardScore(userScore, userId, cardId));
        }
    }

    /**
     * 추천학습 카드 생성
     * @param aiFeedbackResponseDto
     * @param cardId
     * @return
     */
    protected Map<Long, UserFeedbackResponseDto.RecommendCardInfo> createRecommendCard(AiFeedbackResponseDto aiFeedbackResponseDto, Long categoryId){
        Map<Long, UserFeedbackResponseDto.RecommendCardInfo> recommendCard = new HashMap<>();

        //0. 음절인 경우
        if(categoryId < 15){
            UserFeedbackResponseDto.RecommendCardInfo recommendCardInfo = new UserFeedbackResponseDto.RecommendCardInfo("not word");
            recommendCard.put(-1L, recommendCardInfo);
            return recommendCard;
        }

        //1. 100점인 경우 (단어, 문장)
        if(aiFeedbackResponseDto.getUserAccuracy() == 100){
            UserFeedbackResponseDto.RecommendCardInfo recommendCardInfo = new UserFeedbackResponseDto.RecommendCardInfo("perfect");
            recommendCard.put(-100L, recommendCardInfo);
            return recommendCard;
        }

        //2. 문장인데 100점이 아닌 경우
        if(categoryId > 31) {
            UserFeedbackResponseDto.RecommendCardInfo recommendCardInfo = new UserFeedbackResponseDto.RecommendCardInfo("not word");
            recommendCard.put(-1L, recommendCardInfo);
            return recommendCard;
        }

        //3. 100점은 아닌데 추천학습 단어가 없는 경우(단어)
        if(aiFeedbackResponseDto.getRecommendedPronunciations().get(0).equals("-1")){
            UserFeedbackResponseDto.RecommendCardInfo recommendCardInfo = new UserFeedbackResponseDto.RecommendCardInfo("drop the extra sound");
            recommendCard.put(-7L, recommendCardInfo);
            return recommendCard;
        }

        //4. 단어 + 100점이 아닌 경우
        return getWordRecommendCards(aiFeedbackResponseDto.getRecommendedPronunciations(), aiFeedbackResponseDto.getRecommendedLastPronunciations());

    }

    protected Map<Long, UserFeedbackResponseDto.RecommendCardInfo> getWordRecommendCards(List<String> recommendPhonemes, List<String> recommendLastPhonemes){
        Map<Long, UserFeedbackResponseDto.RecommendCardInfo> recommendCard = new HashMap<>();

        //틀린 음소가 4개 이상인 경우
        if(recommendPhonemes.size() + recommendLastPhonemes.size() > 3){
            UserFeedbackResponseDto.RecommendCardInfo recommendCardInfo = new UserFeedbackResponseDto.RecommendCardInfo("try again");
            recommendCard.put(-4L, recommendCardInfo);
            return recommendCard;
        }

        //틀린 음소가 3개 이하일 경우
        recommendPhonemes.forEach(phoneme -> {
            Phoneme foundPhoneme = phonemeRepository.findPhonemeByTextOrderById(phoneme).get(0);
            String hangul = "";
            String text = "";
            Card foundCard = null;
            if(foundPhoneme.getType() == 0){    //초성
                hangul = updatePhonemeService.createHangul(foundPhoneme.getText(), "ㅏ");
                foundCard = cardRepository.findByTextOrderById(hangul).getLast();
                text = "Initial consonant " + phoneme;
            }else if(foundPhoneme.getType() == 1){  //중성
                hangul = updatePhonemeService.createHangul("ㅇ", foundPhoneme.getText());
                foundCard = cardRepository.findByTextOrderById(hangul).getFirst();
                text = "Medial vowel " + phoneme;
            }
            //카테고리 찾기
            Long categoryId = foundCard.getCategoryId();
            Category subCategoryData = categoryRepository.findById(categoryId).get();
            Category categoryData = categoryRepository.findById(subCategoryData.getParentId()).get();
            UserFeedbackResponseDto.RecommendCardInfo recommendCardInfo = new UserFeedbackResponseDto.RecommendCardInfo(text, categoryData.getName(), subCategoryData.getName());
            recommendCard.put(foundCard.getId(), recommendCardInfo);
        });

        //종성
        recommendLastPhonemes.forEach(phoneme -> {
            if (!(phoneme.trim().length() == 0)){
                Long categoryId = getCategoryId(phoneme);
                Card card = cardRepository.findAllByCategoryId(categoryId).get(0);
                Category subCategoryData = categoryRepository.findById(categoryId).get();
                Category categoryData = categoryRepository.findById(subCategoryData.getParentId()).get();
                UserFeedbackResponseDto.RecommendCardInfo recommendCardInfo = new UserFeedbackResponseDto.RecommendCardInfo("final consonant " + phoneme, categoryData.getName(), subCategoryData.getName());
                recommendCard.put(card.getId(), recommendCardInfo);
            }
        });

        return recommendCard;
    }

    //종성 카테고리 아이디 찾기
    protected Long getCategoryId(String phoneme){
        if(phoneme.equals("ㄱ")){
            return 25L;
        }else if(phoneme.equals("ㄴ")){
            return 26L;
        }else if(phoneme.equals("ㄷ")){
            return 27L;
        }else if(phoneme.equals("ㄹ")){
            return 28L;
        }else if(phoneme.equals("ㅁ")){
            return 29L;
        }else if(phoneme.equals("ㅂ")){
            return 30L;
        }else if(phoneme.equals("ㅇ")){
            return 31L;
        }

        return null;
    }

    /**
     * userFeedBackResponseDto 생성
     * @param aiFeedback
     * @param recommendCard
     * @return
     */
    protected UserFeedbackResponseDto setUserFeedbackResponseDto(Long cardId, AiFeedbackResponseDto aiFeedback, Map<Long, UserFeedbackResponseDto.RecommendCardInfo> recommendCard){
        //사용자 오디오 데이터 생성
        UserFeedbackResponseDto.UserAudio userAudio = new UserFeedbackResponseDto.UserAudio(aiFeedback.getUserText(), aiFeedback.getUserMistakenIndexes());
        //waveform 데이터 생성
        UserFeedbackResponseDto.Waveform waveform = new UserFeedbackResponseDto.Waveform(aiFeedback.getUserWaveform(), aiFeedback.getUserAudioDuration(), aiFeedback.getCorrectWaveform(), aiFeedback.getCorrectAudioDuration());

        //객체 생성 및 설정
        UserFeedbackResponseDto userFeedbackResponseDto = new UserFeedbackResponseDto();
        userFeedbackResponseDto.setCardId(cardId);
        userFeedbackResponseDto.setUserAudio(userAudio);
        userFeedbackResponseDto.setUserScore(aiFeedback.getUserAccuracy());
        userFeedbackResponseDto.setRecommendCard(recommendCard);
        userFeedbackResponseDto.setWaveform(waveform);
        
        return userFeedbackResponseDto;
    }

}
