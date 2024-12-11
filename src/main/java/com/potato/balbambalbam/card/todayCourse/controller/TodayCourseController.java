package com.potato.balbambalbam.card.todayCourse.controller;

import com.potato.balbambalbam.card.todayCourse.dto.TodayCourseRequestDto;
import com.potato.balbambalbam.card.todayCourse.dto.TodayCourseResponseDto;
import com.potato.balbambalbam.card.todayCourse.service.TodayCourseService;
import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.user.join.service.JoinService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "TodayCourse API", description = "사용자 학습목표 양에 맞는 카드리스트를 제공")
public class TodayCourseController {
    private final TodayCourseService todayCourseService;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;

    @PostMapping("/cards/today-course")
    @Operation(summary = "todayCourse 요청", description = "사용자의 레벨과 요청 개수에 맞는 카드 리스트를 제공한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드리스트 제공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카테고리 조회", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<TodayCourseResponseDto> getCardList(@RequestBody TodayCourseRequestDto requestDto,
                                                              @RequestHeader("access") String access) {
        Long userId = joinService.findUserBySocialId(jwtUtil.getSocialId(access)).getId();

        TodayCourseResponseDto response = todayCourseService.getCardList(userId, requestDto);

        return ResponseEntity.ok().body(response);

    }
}