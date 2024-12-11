package com.potato.balbambalbam.main.cardList.controller;

import com.potato.balbambalbam.exception.ExceptionDto;
import com.potato.balbambalbam.main.cardList.dto.CardListResponseDto;
import com.potato.balbambalbam.main.cardList.dto.ResponseCardDto;
import com.potato.balbambalbam.main.cardList.service.CardListService;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "CardList API", description = "카테고리에 따른 카드리스트를 제공하고, 북마크를 toggle한다")
public class CardListController {
    private final CardListService cardListService;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;

    @GetMapping ("/cards")
    @Operation(summary = "CardList 조회", description = "parameter에 맞는 카테고리의 카드 리스트를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드리스트 조회", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카테고리 조회", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<CardListResponseDto<List<ResponseCardDto>>> getCardList(@RequestParam("category") String category,
                                                                                  @RequestParam("subcategory") String subcategory,
                                                                                  @RequestHeader("access") String access){
        Long userId = joinService.findUserBySocialId(jwtUtil.getSocialId(access)).getId();

        List<ResponseCardDto> cardDtoList = cardListService.getCardsByCategory(category, subcategory, userId);
        CardListResponseDto<List<ResponseCardDto>> response = new CardListResponseDto<>(cardDtoList, cardDtoList.size());

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/cards/bookmark/{cardId}")
    @Operation(summary = "Card Bookmark 갱신", description = "해당 카드의 북마크 on / off")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 북마크 UPDATE(있으면 삭제 없으면 추가)", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카드", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity updateCardBookmark(@PathVariable("cardId") Integer cardId, @RequestHeader("access") String access){
        Long userId = joinService.findUserBySocialId(jwtUtil.getSocialId(access)).getId();

        String message = cardListService.toggleCardBookmark(Long.valueOf(cardId), userId);
        return ResponseEntity.ok().body(message);
    }
}
