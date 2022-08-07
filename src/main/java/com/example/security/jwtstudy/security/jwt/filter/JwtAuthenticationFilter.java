package com.example.security.jwtstudy.security.jwt.filter;

import com.example.security.jwtstudy.exception.jwt.AccessTokenExpiredException;
import com.example.security.jwtstudy.security.jwt.provider.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
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

    private static final String ACCESS_TOKEN_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_HEADER = "RefreshToken";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter start");
        String accessToken = resolveToken(request, ACCESS_TOKEN_HEADER);

        try {
            if (StringUtils.hasText(accessToken) && jwtTokenProvider.validate(accessToken)) {
                // 검증에 성공하면 토큰으로부터 사용자 정보를 뽑아낸다.
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                log.info("JwtAuthenticationFilter-authentication : {}", authentication);
                // SecurityContextHolder 에 저장시킨다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) { // accessToken 기한 만료는 여기서 잡는다.
            String refreshToken = resolveToken(request, REFRESH_TOKEN_HEADER);
            if (!StringUtils.hasText(refreshToken)) {
                // refreshToken 없으면 예외를 던진다.
                // 던진 예외는 이전 필터에서 잡아서 클라이언트에 알린다.
                throw new AccessTokenExpiredException(e);
            }

            // TODO RefreshToken 검증에 실패하면 예외 발생?
        }

        log.info("JwtAuthenticationFilter end");
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request, String headerName) {
        String bearerToken = request.getHeader(headerName);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
