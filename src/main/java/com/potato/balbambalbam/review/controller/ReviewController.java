package com.potato.balbambalbam.review.controller;

import com.potato.balbambalbam.exception.ExceptionDto;
import com.potato.balbambalbam.review.dto.CardDto;
import com.potato.balbambalbam.review.dto.ReviewListResponseDto;
import com.potato.balbambalbam.review.service.ReviewService;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final ReviewService reviewService;

    @GetMapping("/review")
    @Operation(summary = "복습 CardList 조회", description = "parameter에 맞는 카테고리의 복습 카드 리스트를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드리스트 조회", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카테고리 조회", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<ReviewListResponseDto<List<CardDto>>> getCardList(@RequestParam("category") String category,
                                                                            @RequestParam("subcategory") String subcategory,
                                                                            @RequestHeader("access") String access){
        Long userId = joinService.findUserBySocialId(jwtUtil.getSocialId(access)).getId();

        List<CardDto> cardDtoList = reviewService.getCardsByCategory(category, subcategory, userId);
        ReviewListResponseDto<List<CardDto>> responseDto = new ReviewListResponseDto<>(cardDtoList, cardDtoList.size());

        return ResponseEntity.ok().body(responseDto);
    }

}
