package com.potato.balbambalbam.user.join.service;

import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.user.join.jwt.dto.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findBySocialId(socialId);

        if (user == null) {
            Logger.getLogger(CustomUserDetailsService.class.getName()).log(Level.SEVERE,socialId);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다 : " + socialId);
        }

        return new CustomUserDetails(user);
    }
}