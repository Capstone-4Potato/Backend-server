package com.potato.balbambalbam.user.join.controller;

import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.user.setting.dto.EditResponseDto;
import com.potato.balbambalbam.user.join.dto.JoinResponseDto;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@ResponseBody
@Slf4j
@Tag(name = "Join API", description = "회원가입과 관련된 API를 제공한다.")
public class JoinController {

    private final JoinService joinService;
    private final JWTUtil jwtUtil;

    private Long extractUserIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }


    @Operation(summary = "회원가입", description = "새로운 사용자를 생성한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입이 성공적으로 완료된 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "회원가입이 완료되었습니다."))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청으로 인해 회원가입에 실패한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 회원가입에 실패한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    // 회원정보 받기
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Validated @RequestBody JoinResponseDto joinDto, HttpServletResponse response) {

        joinService.joinProcess(joinDto, response); //access, refresh 토큰 생성

        return ResponseEntity.ok().body("회원가입이 완료되었습니다."); //200
    }

    @Operation(summary = "회원정보 조회", description = "사용자의 회원정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 회원정보를 반환한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EditResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자가 접근하려고 하는 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없는 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 회원정보 조회에 실패한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    //회원정보 출력
    @GetMapping("/users")
    public ResponseEntity<?> getUserById(@RequestHeader("access") String access) {

        Long userId = extractUserIdFromToken(access);
        EditResponseDto editUser = joinService.findUserById(userId);

        return ResponseEntity.ok().body(editUser); //200
    }

}
