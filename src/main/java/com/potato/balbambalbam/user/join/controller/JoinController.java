package com.potato.balbambalbam.user.join.controller;

import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.user.join.jwt.dto.DeleteUserDto;
import com.potato.balbambalbam.user.join.jwt.dto.EditDto;
import com.potato.balbambalbam.user.join.jwt.dto.JoinDto;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@Slf4j
public class JoinController {

    private final JoinService joinService;
    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    public JoinController(JoinService joinService,
                          JWTUtil jwtUtil,
                          RefreshRepository refreshRepository) {
        this.joinService = joinService;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    private Long extractUserIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    private String extractSocialIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return socialId;
    }

    // 회원정보 받기
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Validated @RequestBody JoinDto joinDto, HttpServletResponse response) {

        joinService.joinProcess(joinDto, response); //access, refresh 토큰 생성
        log.info("회원가입이 완료되었습니다.");

        return ResponseEntity.ok().body("회원가입이 완료되었습니다."); //200
    }

    // 회원정보 수정
    @PatchMapping("/users")
    public ResponseEntity<?> updateUser(@Validated @RequestHeader("access") String access,
                                        @RequestBody JoinDto joinDto) {

        Long userId = extractUserIdFromToken(access);
        EditDto editUser = joinService.updateUser(userId, joinDto);
        log.info("{} : 회원정보 수정이 완료되었습니다.", userId);

        return ResponseEntity.ok().body(editUser); //200
    }

    //회원정보 삭제
    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestHeader("access") String access,
                                        @RequestBody DeleteUserDto deleteUserDto) {

        Long userId = extractUserIdFromToken(access);
        String name = deleteUserDto.getName();
        joinService.deleteUser(userId, name);

        // refresh
        String socialID = extractSocialIdFromToken(access);
        String refresh = refreshRepository.findRefreshBySocialId(socialID);

        if (refresh != null && refreshRepository.existsByRefresh(refresh)) {
            refreshRepository.deleteByUserId(userId);
        }

        log.info("{} : 회원 탈퇴가 완료되었습니다.", userId);
        return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다."); //200
    }

    //회원정보 출력
    @GetMapping("/users")
    public ResponseEntity<?> getUserById(@RequestHeader("access") String access) {

        Long userId = extractUserIdFromToken(access);
        EditDto editUser = joinService.findUserById(userId);

        log.info("{} : 회원정보가 출력되었습니다.", userId);
        return ResponseEntity.ok().body(editUser); //200
    }

}
