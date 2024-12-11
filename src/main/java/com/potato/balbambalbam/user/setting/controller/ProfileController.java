package com.potato.balbambalbam.user.setting.controller;

import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.user.setting.dto.DeleteUserResponseDto;
import com.potato.balbambalbam.user.setting.dto.EditResponseDto;
import com.potato.balbambalbam.user.setting.service.ProfileService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
@ResponseBody
@Slf4j
@Tag(name = "Profile API", description = "Profile과 관련된 API를 제공한다.")
public class ProfileController {
    private final ProfileService profileService;
    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    private Long extractUserIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return profileService.findUserBySocialId(socialId).getId();
    }

    private String extractSocialIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return socialId;
    }

    @Operation(summary = "회원정보 수정", description = "기존 사용자의 정보를 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보가 성공적으로 수정된 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EditResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청으로 인해 회원정보 수정에 실패한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자가 접근하려고 하는 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 회원정보 수정에 실패한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    // 회원정보 수정
    @PatchMapping("/users")
    public ResponseEntity<?> updateUser(@Validated @RequestHeader("access") String access,
                                        @RequestBody EditResponseDto editResponseDto) {

        Long userId = extractUserIdFromToken(access);
        EditResponseDto editUser = profileService.updateUser(userId, editResponseDto);

        return ResponseEntity.ok().body(editUser); //200
    }

    @Operation(summary = "회원 탈퇴", description = "기존 사용자의 계정을 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴가 성공적으로 완료된 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "회원 탈퇴가 완료되었습니다."))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청으로 인해 회원 탈퇴에 실패한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자가 접근하려고 하는 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 회원 탈퇴에 실패한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    //회원정보 삭제
    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestHeader("access") String access,
                                        @RequestBody DeleteUserResponseDto deleteUserDto) {

        Long userId = extractUserIdFromToken(access);
        String name = deleteUserDto.getName();
        profileService.deleteUser(userId, name);

        // refresh
        String socialID = extractSocialIdFromToken(access);
        String refresh = refreshRepository.findRefreshBySocialId(socialID);

        if (refresh != null && refreshRepository.existsByRefresh(refresh)) {
            refreshRepository.deleteByUserId(userId);
        }

        return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다."); //200
    }
}
