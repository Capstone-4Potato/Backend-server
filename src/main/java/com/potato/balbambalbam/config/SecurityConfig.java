package com.potato.balbambalbam.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.user.join.jwt.CustomLogoutFilter;
import com.potato.balbambalbam.user.join.jwt.JWTFilter;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final ObjectMapper objectMapper;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                          JWTUtil jwtUtil,
                          RefreshRepository refreshRepository,
                          ObjectMapper objectMapper) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.objectMapper = objectMapper;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        http
                .authorizeHttpRequests((auth) -> auth.anyRequest().permitAll());
                        /*.requestMatchers("/login","/users").permitAll()
                        .anyRequest().authenticated());*/

        //jwt 필터 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil, objectMapper), LoginFilter.class);

        //로그인 필터 추가
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository,objectMapper), UsernamePasswordAuthenticationFilter.class);

        //로그아웃 필터 추가
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository,objectMapper), LogoutFilter.class);

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
