package com.potato.balbambalbam.main.cardList.controller;

import com.potato.balbambalbam.exception.ExceptionDto;
import com.potato.balbambalbam.main.cardList.service.CardWeakSoundService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "CardWeakSound API", description = "취약음 리스트에 따라 카드들의 취약음 여부를 모두 갱신한다")
public class CardWeakSoundController {
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final CardWeakSoundService cardWeakSoundService;

    @GetMapping("/cards/weaksound")
    @Operation(summary = "Card WeakSound 갱신", description = "사용자 취약음 갱신 시 전체 카드에 대한 취약음 여부 갱신")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : Card WeakSound Update 성공)", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : Update 실패", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity updateCardWeakSound(@RequestHeader("access") String access){
        Long userId = joinService.findUserBySocialId(jwtUtil.getSocialId(access)).getId();

        String message = cardWeakSoundService.updateCardWeakSound(userId);
        return ResponseEntity.ok().body(message);
    }
}
