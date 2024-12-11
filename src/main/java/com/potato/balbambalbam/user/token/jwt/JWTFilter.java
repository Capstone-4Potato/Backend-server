package com.potato.balbambalbam.user.token.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.user.token.service.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JWTFilter(JWTUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader("access");

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "ExpiredJwtException", "access 토큰이 만료되었습니다."); //401
            return;
        }

        String socialId = jwtUtil.getSocialId(accessToken);
        String role = jwtUtil.getRole(accessToken);

        User user = new User();
        user.setSocialId(socialId);
        user.setRole(role);
        CustomUserDetails customUserDetails = new CustomUserDetails(Optional.of(user));

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
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