package com.potato.balbambalbam.main.customCard.controller;

import com.potato.balbambalbam.exception.CardDeleteException;
import com.potato.balbambalbam.exception.ExceptionDto;
import com.potato.balbambalbam.main.customCard.dto.CustomCardRequestDto;
import com.potato.balbambalbam.main.customCard.dto.CustomCardResponseDto;
import com.potato.balbambalbam.main.customCard.service.CustomCardService;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "CustomCard 생성 & 삭제", description = "CustomCard를 생성하거나 삭제한다")
public class CustomCardController {
    private final CustomCardService customCardService;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    @PostMapping("/cards/custom")
    @Operation(summary = "customCard 생성", description = "text 해당하는 custom card를 생성한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드 생성 완료", content = @Content(schema = @Schema(implementation = CustomCardResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ERROR : 카드를 생성 불가(10개 이상 or 한국어 X, 35자 이상)", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<CustomCardResponseDto> postCustomCard(@Validated @RequestBody CustomCardRequestDto customCardRequestDto, @RequestHeader("access") String access) {
        Long userId = joinService.findUserBySocialId(jwtUtil.getSocialId(access)).getId();

        CustomCardResponseDto customCardResponse = customCardService.createCustomCardIfPossible(customCardRequestDto.getText(), userId);

        return ResponseEntity.ok().body(customCardResponse);
    }

    @DeleteMapping("/cards/custom/{cardId}")
    @Operation(summary = "customCard 삭제", description = "원하는 custom card를 삭제한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드 삭제 완료"),
            @ApiResponse(responseCode = "400", description = "ERROR : 카드 삭제 불가", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity deleteCustomCard(@PathVariable("cardId") Long cardId, @RequestHeader("access") String access) throws CardDeleteException {
        Long userId = joinService.findUserBySocialId(jwtUtil.getSocialId(access)).getId();

        boolean isDeleted = customCardService.deleteCustomCard(userId, cardId);

        if(!isDeleted){
            throw new CardDeleteException(cardId + " : 카드 삭제 실패하였습니다");
        }

        return ResponseEntity.ok("카드 삭제 완료");
    }
}
