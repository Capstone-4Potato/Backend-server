package com.potato.balbambalbam.user.join.controller;

import com.potato.balbambalbam.data.entity.Refresh;
import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.exception.ResponseNotFoundException;
import com.potato.balbambalbam.exception.TokenExpiredException;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ReissueController(JWTUtil jwtUtil, RefreshRepository refreshRepository){
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = request.getHeader("refresh");

        if (refresh == null) {
            throw new ResponseNotFoundException("refresh 토큰이 없습니다."); // 404
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            refreshRepository.deleteByRefresh(refresh);
            log.info("refresh 토큰이 만료되었습니다.");
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
        addRefreshEntity(socialId, newRefresh, 864000000L);

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
