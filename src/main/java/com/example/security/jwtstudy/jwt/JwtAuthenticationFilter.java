package com.example.security.jwtstudy.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("jwt filter start");
        // 토큰 확인
        // 검증 된 토큰이면 SecurityContext 에 담고, 수행
        String jwtToken = resolveToken(request);
        log.info("jwt : {}", jwtToken);

        // token 검증
        if (StringUtils.hasText(jwtToken) && jwtTokenProvider.validate(jwtToken)) {
            // 검증에 성공하면 토큰으로부터 사용자 정보를 뽑아낸다.
            Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
            // SecurityContextHolder 에 저장시킨다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        log.info("jwt filter end");
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
