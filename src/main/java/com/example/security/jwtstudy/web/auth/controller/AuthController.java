package com.example.security.jwtstudy.web.auth.controller;

import com.example.security.jwtstudy.domain.refreshtoken.entity.RefreshToken;
import com.example.security.jwtstudy.domain.refreshtoken.service.RefreshTokenService;
import com.example.security.jwtstudy.domain.user.entity.User;
import com.example.security.jwtstudy.exception.jwt.InvalidTokenException;
import com.example.security.jwtstudy.exception.jwt.NotExpiredTokenException;
import com.example.security.jwtstudy.security.jwt.JwtTokenUtils;
import com.example.security.jwtstudy.web.auth.dto.LoginRequestDto;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenService refreshTokenService;

    // 로그인 : access + refresh 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto) {
        log.info("login start");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getLoginId(), requestDto.getPassword());
        Authentication authenticate =
                authenticationManagerBuilder.getObject()
                        .authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        Map<String, String> tokens = jwtTokenUtils.createTokens(authenticate);

        log.info("login end");
        return ResponseEntity.ok(tokens);
    }

    // accessToken 재발급
    @PostMapping("/reissue/access")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request) {
        log.info("reissue accessToken start");

        Map<String, String> result = new HashMap<>();

        String accessToken = resolveAccessToken(request);
        try {
            if (StringUtils.hasText(accessToken) && jwtTokenUtils.validate(accessToken)) {
                // TODO 검증 성공시 예외 처리
                throw new NotExpiredTokenException("토큰이 만료되지 않았습니다.");
            }
        } catch (ExpiredJwtException e) {
            RefreshToken refreshToken =
                    refreshTokenService.findByTokenFetch(resolveRefreshToken(request))
                            .orElseThrow(() -> new InvalidTokenException("유효하지 않은 토큰입니다."));

            jwtTokenUtils.validate(refreshToken.getToken());

            User user = refreshToken.getUser();
            List<SimpleGrantedAuthority> authorities =
                    Arrays.stream(new String[]{user.getRole().name()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getId().toString(), "", authorities);
            result.put("accessToken", jwtTokenUtils.createAccessToken(authenticationToken));
        }

        log.info("reissue accessToken end");
        return ResponseEntity.ok(result);
    }

    // accessToken + refreshToken 재발급
    @PostMapping("/reissue/all")
    public ResponseEntity<?> reissueTokens(HttpServletRequest request) {
        log.info("reissue all Tokens start");

        Map<String, String> result = new HashMap<>();

        String accessToken = resolveAccessToken(request);
        try {
            if (StringUtils.hasText(accessToken) && jwtTokenUtils.validate(accessToken)) {
                throw new NotExpiredTokenException("토큰이 만료되지 않았습니다.");
            }
        } catch (ExpiredJwtException e) {
            RefreshToken refreshToken =
                    refreshTokenService.findByTokenFetch(resolveRefreshToken(request))
                            .orElseThrow(() -> new InvalidTokenException("유효하지 않은 토큰입니다."));

            jwtTokenUtils.validate(refreshToken.getToken());

            User user = refreshToken.getUser();
            List<SimpleGrantedAuthority> authorities =
                    Arrays.stream(new String[]{user.getRole().name()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getId().toString(), "", authorities);
            result.put("accessToken", jwtTokenUtils.createAccessToken(authenticationToken));
            result.put("refreshToken", jwtTokenUtils.reissueRefreshToken(authenticationToken, refreshToken));
        }

        log.info("reissue all Tokens end");
        return ResponseEntity.ok(result);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        String accessToken = request.getHeader("RefreshToken");
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        return null;
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // TODO Use Redis

        return ResponseEntity.ok(null);
    }

}
