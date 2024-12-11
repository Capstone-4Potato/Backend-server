package com.potato.balbambalbam.myReport.test.controller;

import com.potato.balbambalbam.data.entity.WeakSoundTest;
import com.potato.balbambalbam.data.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.exception.InvalidParameterException;
import com.potato.balbambalbam.exception.ParameterNotFoundException;
import com.potato.balbambalbam.myReport.test.dto.TestStartResponseDto;
import com.potato.balbambalbam.myReport.test.dto.WeakSoundTestListDto;
import com.potato.balbambalbam.myReport.test.service.WeakSoundTestService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Tag(name = "WeakSoundTest API", description = "사용자의 취약음소 테스트와 관련된 API를 제공한다.")
public class WeakSoundTestController {

    private final WeakSoundTestRepository weakSoundTestRepository;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final WeakSoundTestService weakSoundTestService;

    private Long extractUserIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @Operation(summary = "취약음소 테스트 존재 여부 확인", description = "이전에 진행중이던 테스트가 있는지 확인한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "확인 완료", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestStartResponseDto.class)))
    })
    @GetMapping("/test/check")
    public ResponseEntity<TestStartResponseDto> checkTest(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        TestStartResponseDto response = weakSoundTestService.checkTestStatus(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "새로운 테스트 시작", description = "이전 테스트 데이터를 삭제하고 새로운 테스트를 시작한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 취약음소 테스트 목록을 반환한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WeakSoundTestListDto.class)))
    })
    @PostMapping("/test/new")
    public ResponseEntity<List<WeakSoundTestListDto>> startNewTest(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        weakSoundTestService.startNewTest(userId);
        return ResponseEntity.ok(weakSoundTestService.getAllTests());
    }

    @Operation(summary = "테스트 이어하기", description = "마지막으로 진행했던 테스트 이후부터 목록을 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 취약음소 테스트 목록을 반환한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WeakSoundTestListDto.class))),
            @ApiResponse(responseCode = "404", description = "진행중인 테스트를 찾을 수 없는 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/test/continue")
    public ResponseEntity<List<WeakSoundTestListDto>> continueTest(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        List<WeakSoundTestListDto> remainingTests = weakSoundTestService.getContinueTests(userId);
        return ResponseEntity.ok(remainingTests);
    }

    @Operation(summary = "사용자 음성 파일 업로드 및 테스트", description = "사용자 음성 파일을 업로드하고 AI와의 테스트 결과를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 테스트 결과를 반환한 경우", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"userWeakPhoneme\": {\"ㄲ\" : 5, \"ㅏ\" : 2, \" \" : 1}, \"userWeakPhonemeLast\": {\"ㄱ\" : 2}}"))),
            @ApiResponse(responseCode = "400", description = "사용자 음성 파일이 비어있는 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID를 가진 테스트 카드를 찾을 수 없는 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping(value = "/test/{cardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@PathVariable("cardId") Long id, @RequestHeader("access") String access, @RequestParam("userAudio") MultipartFile userAudio) throws IOException {
        Long userId = extractUserIdFromToken(access);

        if (userAudio.isEmpty()) {
            throw new ParameterNotFoundException("사용자 음성 파일이 비었습니다.");
        }

        WeakSoundTest weakSoundTest = weakSoundTestRepository.findById(id)
                .orElseThrow(() -> new InvalidParameterException("해당 id를 가진 테스트 카드가 없습니다."));

        String testResponseJson = weakSoundTestService.processUserAudio(userAudio, weakSoundTest, userId);
        return ResponseEntity.ok(testResponseJson);
    }

    @Operation(summary = "취약음소 분석 완료", description = "사용자의 취약음소 분석을 완료하고, 결과를 저장한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취약음소가 있는 경우", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"1\": 3, \"14\": 2, \"40\": 1, \"phonemeId\": \"count\"}"))),
            @ApiResponse(responseCode = "404", description = "취약음소가 없는 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/test/finalize")
    public ResponseEntity<Map<Long, Integer>> finalizeAnalysis(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        Map<Long, Integer> topPhonemes = weakSoundTestService.getTopPhonemes(userId);
        weakSoundTestService.finalizeTestStatus(userId);
        return ResponseEntity.ok(topPhonemes);
    }
}