package com.potato.balbambalbam.user.join.service;

import com.potato.balbambalbam.data.entity.Refresh;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.user.setting.dto.EditResponseDto;
import com.potato.balbambalbam.user.join.dto.JoinResponseDto;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class JoinService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserLevelRepository userLevelRepository;

    //새로운 회원정보 저장
    @Transactional
    public void joinProcess(JoinResponseDto joinDto, HttpServletResponse response) {
        String name = joinDto.getName();
        String socialId = joinDto.getSocialId();
        Integer age = joinDto.getAge();
        Byte gender = joinDto.getGender();

        // 사용자 데이터베이스에 회원정보 저장
        User data = new User();
        data.setName(name);
        data.setSocialId(socialId);
        data.setAge(age);
        data.setGender(gender);
        data.setRole("ROLE_USER");
        User savedUser = userRepository.save(data);

        // 사용자 레벨 데이터베이스에 저장
        UserLevel userLevel = new UserLevel();
        userLevel.setUserId(savedUser.getId());
        userLevel.setUserExperience(0L);
        userLevel.setLevelId(1L);
        userLevel.setCategoryId(1L);
        userLevelRepository.save(userLevel);

        // access 토큰 발급
        String access = jwtUtil.createJwt("access", socialId, data.getRole(), 7200000L); // 7200000L 120분, 120000L 2분

        // Refresh 토큰 발급
        String refresh = jwtUtil.createJwt("refresh", socialId, data.getRole(),
                864000000L); // 86400000L 24시간, 300000L 5분
        addRefreshEntity(socialId, refresh, 864000000L);

        response.setHeader("access", access);
        response.setHeader("refresh", refresh);
    }

    private void addRefreshEntity(String socialId, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setSocialId(socialId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    //회원정보 검색
    public EditResponseDto findUserById(Long userId) {
        User editUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")); //404
        return new EditResponseDto(editUser.getName(), editUser.getAge(), editUser.getGender());
    }

    public User findUserBySocialId(String socialId) {
        return userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")); //404
    }

}