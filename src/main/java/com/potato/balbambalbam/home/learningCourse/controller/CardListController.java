package com.potato.balbambalbam.home.learningCourse.controller;

import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.home.learningCourse.dto.CardListResponseDto;
import com.potato.balbambalbam.home.learningCourse.dto.CourseResponseDto;
import com.potato.balbambalbam.home.learningCourse.dto.ResponseCardDto;
import com.potato.balbambalbam.home.learningCourse.service.CardListService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
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
@Tag(name = "LearningCourse API", description = "카테고리에 따른 카드리스트를 제공")
public class CardListController {
    private final CardListService cardListService;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;

    @GetMapping ("/home/course/{level}")
    @Operation(summary = "CardList 조회", description = "레벨에 맞는 카테고리의 카드 리스트를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드리스트 조회", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카테고리 조회", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<CardListResponseDto<List<ResponseCardDto>>> getCardList(@RequestParam("level") Long level,
                                                                                  @RequestHeader("access") String access){
        Long userId = joinService.findUserBySocialId(jwtUtil.getSocialId(access)).getId();

        List<ResponseCardDto> cardDtoList = cardListService.getCardsByCategory(level, userId);
        CardListResponseDto<List<ResponseCardDto>> response = new CardListResponseDto<>(cardDtoList, cardDtoList.size());

        return ResponseEntity.ok().body(response);

    }

    @GetMapping ("/home/course")
    @Operation(summary = "course 학습 현황 조회", description = "레벨에 맞는 카테고리의 카드 리스트를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 조회 실패", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<CourseResponseDto> getCourseList(@RequestHeader("access") String access){
        Long userId = joinService.findUserBySocialId(jwtUtil.getSocialId(access)).getId();

        CourseResponseDto response = cardListService.getCourseList(userId);

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
