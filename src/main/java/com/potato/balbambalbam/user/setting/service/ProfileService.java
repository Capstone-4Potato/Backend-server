package com.potato.balbambalbam.user.setting.service;

import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CardBookmarkRepository;
import com.potato.balbambalbam.data.repository.CardScoreRepository;
import com.potato.balbambalbam.data.repository.CardWeakSoundRepository;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.data.repository.UserAttendanceRepository;
import com.potato.balbambalbam.data.repository.UserLevelRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.data.repository.UserWeakSoundRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestSatusRepositoy;
import com.potato.balbambalbam.exception.InvalidUserNameException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.user.setting.dto.EditResponseDto;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final CardScoreRepository cardScoreRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;
    private final CustomCardRepository customCardRepository;
    private final UserWeakSoundRepository userWeakSoundRepository;
    private final WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy;
    private final UserLevelRepository userLevelRepository;
    private final UserAttendanceRepository userAttendanceRepository;

    // 회원정보 업데이트
    @Transactional
    public EditResponseDto updateUser(Long userId, EditResponseDto editResponseDto) {
        User editUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")); //404

        editUser.setName(editResponseDto.getName());
        editUser.setAge(editResponseDto.getAge());
        editUser.setGender(editResponseDto.getGender());

        userRepository.save(editUser);

        return new EditResponseDto(editUser.getName(), editUser.getAge(), editUser.getGender());
    }

    //회원정보 삭제
    @Transactional
    public void deleteUser(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        if (!user.getName().equals(name)) {
            throw new InvalidUserNameException("닉네임이 일치하지 않습니다."); //400
        }

        if (cardBookmarkRepository.existsByUserId(userId)) {
            cardBookmarkRepository.deleteByUserId(userId);
        }
        if (cardScoreRepository.existsByUserId(userId)) {
            cardScoreRepository.deleteByUserId(userId);
        }
        if (cardWeakSoundRepository.existsByUserId(userId)) {
            cardWeakSoundRepository.deleteByUserId(userId);
        }
        if (customCardRepository.existsByUserId(userId)) {
            customCardRepository.deleteUserById(userId);
        }
        if (userWeakSoundRepository.existsByUserId(userId)) {
            userWeakSoundRepository.deleteByUserId(userId);
        }
        if (weakSoundTestSatusRepositoy.existsByUserId(userId)) {
            weakSoundTestSatusRepositoy.deleteByUserId(userId);
        }
        if (userLevelRepository.existsByUserId(userId)) {
            userLevelRepository.deleteByUserId(userId);
        }
        if (userAttendanceRepository.existsByUserId(userId)) {
            userAttendanceRepository.deleteByUserId(userId);
        }

        refreshRepository.deleteBySocialId(user.getSocialId());
        userRepository.deleteById(userId);
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
