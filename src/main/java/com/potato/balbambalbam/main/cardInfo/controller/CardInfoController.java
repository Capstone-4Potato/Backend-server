package com.potato.balbambalbam.main.cardInfo.controller;

import com.potato.balbambalbam.main.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.main.cardInfo.service.CardInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.charset.Charset;

@Controller
@RequiredArgsConstructor
public class CardInfoController {
    //TODO : user 동적으로 할당
    public static final long TEMPORARY_USER_ID = 1L;
    private final CardInfoService cardInfoService;

    @GetMapping("/cards/{cardId}")
    @Operation(summary = "card info 제공", description = "카드 id, 카드 text, 발음 text, 맞춤 음성 제공")
    @ApiResponses()
    public ResponseEntity<CardInfoResponseDto> postCardInfo(@PathVariable("cardId") Long cardId) {
        CardInfoResponseDto cardInfoResponseDto = cardInfoService.getCardInfo(cardId, TEMPORARY_USER_ID);

        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
        headers.setContentType(mediaType);

        return ResponseEntity.ok().headers(headers).body(cardInfoResponseDto);
    }
}
