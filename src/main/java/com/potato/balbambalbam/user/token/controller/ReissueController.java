package com.potato.balbambalbam.user.token.controller;

import com.potato.balbambalbam.data.entity.Refresh;
import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.exception.ResponseNotFoundException;
import com.potato.balbambalbam.exception.TokenExpiredException;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@Tag(name = "RefreshToken API", description = "자동 로그인과 관련된 API를 제공한다.")
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ReissueController(JWTUtil jwtUtil, RefreshRepository refreshRepository){
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Operation(summary = "토큰 재발급", description = "만료된 또는 유효한 refresh 토큰을 이용하여 새로운 access 및 refresh 토큰을 재발급한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급이 성공적으로 완료된 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "refresh 토근과 access 토큰이 재발급 되었습니다."))),
            @ApiResponse(responseCode = "404", description = "refresh 토큰이 요청에 포함되지 않은 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "refresh 토큰이 만료된 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 토큰 재발급에 실패한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader("refresh") String refresh, HttpServletResponse response) {

        if (refresh == null) {
            throw new ResponseNotFoundException("refresh 토큰이 없습니다."); // 404
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            refreshRepository.deleteByRefresh(refresh);
            // Refresh 토큰 삭제
            refreshRepository.deleteByRefresh(refresh);
            throw new TokenExpiredException("refresh 토큰이 만료되었습니다."); // 401
        }

        String socialId = jwtUtil.getSocialId(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", socialId, role, 7200000L);
        String newRefresh = jwtUtil.createJwt("refresh", socialId, role, 864000000L);


        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteBySocialId(socialId);
        addRefreshEntity(socialId, newRefresh, 864000000L); // 10일

        response.setHeader("access", newAccess);
        response.setHeader("refresh", newRefresh);

        return new ResponseEntity<>("refresh 토근과 access 토큰이 재발급 되었습니다.",HttpStatus.OK);//200
    }

    private void addRefreshEntity(String socialId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setSocialId(socialId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}
