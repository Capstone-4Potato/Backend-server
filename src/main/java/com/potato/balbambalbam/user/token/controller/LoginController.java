package com.potato.balbambalbam.user.token.controller;

import com.potato.balbambalbam.exception.dto.ExceptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Login", description = "로그인을 통해 access 토큰과 refresh 토큰을 받을 수 있다.")
public class LoginController {
    @Operation(summary = "Login  API", description = "access 토큰과 refresh 토큰을 받을 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인이 완료되었습니다.", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "socialId가 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
    })

    @PostMapping("/login")
    public String login( @RequestParam String socialId) {
        return "로그인이 완료되었습니다.";
    }
}
