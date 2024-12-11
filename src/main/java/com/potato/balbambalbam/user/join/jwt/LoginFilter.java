package com.potato.balbambalbam.user.join.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.data.entity.Refresh;
import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.exception.ExceptionDto;
import com.potato.balbambalbam.user.join.jwt.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final ObjectMapper objectMapper;

    public LoginFilter(AuthenticationManager authenticationManager,
                       JWTUtil jwtUtil,
                       RefreshRepository refreshRepository,
                       ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        String socialId = request.getParameter("socialId");

        if (socialId == null) {
            throw new AuthenticationServiceException("socialId가 없습니다.");
        }

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(socialId, "");
        return authenticationManager.authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String socialId = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", socialId, role, 7200000L); // 7200000L 2시간
        String refresh = jwtUtil.createJwt("refresh", socialId, role, 864000000L); // 86400000L 24시간

        response.setHeader("access", access);
        response.setHeader("refresh", refresh);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteBySocialId(socialId);
        addRefreshEntity(socialId, refresh, 864000000L);

        log.info("로그인이 완료되었습니다.");
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().print("로그인이 완료되었습니다.");
    }

    private void addRefreshEntity(String socialId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setSocialId(socialId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        log.info("로그인에 실패하였습니다.");

        if (failed instanceof UsernameNotFoundException) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "UsernameNotFoundException", "존재하지 않은 사용자 아이디입니다."); // 404
        } else if (failed instanceof AuthenticationServiceException) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "AuthenticationServiceException", "socialId가 없습니다."); // 404
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "AuthenticationException", "서버 오류가 발생했습니다."); // 500
        }
    }

    private void sendError(HttpServletResponse response, int statusCode, String exceptionName, String message) throws IOException {

        ExceptionDto exceptionDto = new ExceptionDto(statusCode, exceptionName, message);
        response.setStatus(statusCode);
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(objectMapper.writeValueAsString(exceptionDto));
        writer.flush();
    }
}