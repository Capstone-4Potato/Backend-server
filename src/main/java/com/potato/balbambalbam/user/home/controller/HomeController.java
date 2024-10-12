package com.potato.balbambalbam.user.home.controller;

import com.potato.balbambalbam.log.dto.ExceptionDto;
import com.potato.balbambalbam.user.home.dto.HomeInfoDto;
import com.potato.balbambalbam.user.join.service.JoinService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final JoinService joinService;
    private final JWTUtil jwtUtil;

    public HomeController (JoinService joinService, JWTUtil jwtUtil){
        this.joinService = joinService;
        this.jwtUtil = jwtUtil;
    }
    private Long extractUserIdFromToken(String access) { // access 토큰으로부터 userId 추출하는 함수
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @Operation(summary = "홈 화면 정보 조회", description = "사용자의 홈 화면에 필요한 모든 정보를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "홈 화면 정보 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HomeInfoDto.class))
            ),
            @ApiResponse(
                    responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 오류로 인해 정보 조회에 실패한 경우",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))
            )
    })
    @GetMapping("/home")
    public ResponseEntity<HomeInfoDto> getHomeInfo(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);

        HomeInfoDto response = new HomeInfoDto();
        return ResponseEntity.ok(response);
    }
}
