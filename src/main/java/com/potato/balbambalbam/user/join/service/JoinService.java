package com.potato.balbambalbam.user.join.service;

import com.potato.balbambalbam.data.entity.Refresh;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.InvalidUserNameException;
import com.potato.balbambalbam.exception.SocialIdChangeException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.user.join.jwt.dto.EditDto;
import com.potato.balbambalbam.user.join.jwt.dto.JoinDto;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final CardScoreRepository cardScoreRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;
    private final CustomCardRepository customCardRepository;
    private final UserWeakSoundRepository userWeakSoundRepository;
    private final WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy;

    public JoinService(UserRepository userRepository,
                       JWTUtil jwtUtil,
                       RefreshRepository refreshRepository,
                       CardBookmarkRepository cardBookmarkRepository,
                       CardScoreRepository cardScoreRepository,
                       CardWeakSoundRepository cardWeakSoundRepository,
                       CustomCardRepository customCardRepository,
                       UserWeakSoundRepository userWeakSoundRepository,
                       WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.cardBookmarkRepository = cardBookmarkRepository;
        this.cardScoreRepository = cardScoreRepository;
        this.cardWeakSoundRepository = cardWeakSoundRepository;
        this.customCardRepository = customCardRepository;
        this.userWeakSoundRepository = userWeakSoundRepository;
        this.weakSoundTestSatusRepositoy = weakSoundTestSatusRepositoy;
    }

    //새로운 회원정보 저장
    public void joinProcess(JoinDto joinDto, HttpServletResponse response) {

        String name = joinDto.getName();
        String socialId = joinDto.getSocialId();
        Integer age = joinDto.getAge();
        Byte gender = joinDto.getGender();

        //데이터베이스에 회원정보 저장
        User data = new User();
        data.setName(name);
        data.setSocialId(socialId);
        data.setAge(age);
        data.setGender(gender);
        data.setRole("ROLE_USER");
        userRepository.save(data);

        //access 토큰 발급
        String access = jwtUtil.createJwt("access", socialId, data.getRole(), 7200000L); // 7200000L 120분, 120000L 2분

        // Refresh 토큰 발급
        String refresh = jwtUtil.createJwt("refresh", socialId, data.getRole(), 864000000L); // 86400000L 24시간, 300000L 5분
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

    // 회원정보 업데이트
    @Transactional
    public EditDto updateUser(Long userId, JoinDto joinDto) {

        User editUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")); //404

        if (joinDto.getSocialId() != null) {
            throw new SocialIdChangeException("이메일 변경은 허용되지 않습니다."); //400
        }

        editUser.setName(joinDto.getName());
        editUser.setAge(joinDto.getAge());
        editUser.setGender(joinDto.getGender());

        userRepository.save(editUser);

        return new EditDto(editUser.getName(), editUser.getAge(), editUser.getGender());
    }

    //회원정보 삭제
    @Transactional
    public void deleteUser(Long userId, String name){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")); //404

        if(!user.getName().equals(name)){
            throw new InvalidUserNameException("닉네임이 일치하지 않습니다."); //400
        }


        if(cardBookmarkRepository.existsByUserId(userId)){
            cardBookmarkRepository.deleteByUserId(userId);

        }
        if(cardScoreRepository.existsByUserId(userId)){
            cardScoreRepository.deleteByUserId(userId);
        }
        if(cardWeakSoundRepository.existsByUserId(userId)){
            cardWeakSoundRepository.deleteByUserId(userId);

        }
        if(customCardRepository.existsByUserId(userId)){
            customCardRepository.deleteUserById(userId);
        }
        if(userWeakSoundRepository.existsByUserId(userId)){
            userWeakSoundRepository.deleteByUserId(userId);

        }
        if(weakSoundTestSatusRepositoy.existsByUserId(userId)){
            weakSoundTestSatusRepositoy.deleteByUserId(userId);
        }

        // Refresh 토큰 삭제
        refreshRepository.deleteByUserId(userId);

        userRepository.deleteById(userId);

    }

    //회원정보 검색
    public EditDto findUserById(Long userId){
        User editUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")); //404
        return new EditDto(editUser.getName(), editUser.getAge(), editUser.getGender());
    }

    public User findUserBySocialId(String socialId) {
        return userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")); //404
    }

}