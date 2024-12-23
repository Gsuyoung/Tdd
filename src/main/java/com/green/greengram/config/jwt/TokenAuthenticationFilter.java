package com.green.greengram.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
//extends를 했는데 빨간줄이 뜬다면 1.부모가 기본생성자가 없을 때 2.추상메소드를 가지고 있을 때
public class TokenAuthenticationFilter extends OncePerRequestFilter { //상속(추상클래스)
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("ip Address: {}", request.getRemoteAddr());
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION); //프론트에서 넘어올 때 : "Bearer 토큰값"
        log.info("authorizationHeader: {}", authorizationHeader);

        String token = getAccessToken(authorizationHeader);
        log.info("token: {}", token);

        if (token!= null) {
            boolean result = false;
            try {
                Authentication auth = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                request.setAttribute("exception", e);
            }
        }
        filterChain.doFilter(request, response); //doFilter를 하면 다음 filter로 넘어간다.
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) { //Bearer로 시작하거나 null이 아니라면
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
