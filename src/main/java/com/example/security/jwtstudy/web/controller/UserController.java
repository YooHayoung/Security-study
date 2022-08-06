package com.example.security.jwtstudy.web.controller;

import com.example.security.jwtstudy.jwt.JwtTokenUtils;
import com.example.security.jwtstudy.web.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> signup() {
        // 회원 가입 로직

        return ResponseEntity.ok(null);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getLoginId(), requestDto.getPassword());
        Authentication authenticate =
                authenticationManagerBuilder.getObject()
                        .authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        Map<String, String> tokens = jwtTokenUtils.createTokens(authenticate);

        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello(@AuthenticationPrincipal User user) {
        // 유저 정보 출력 로직
        log.info("memberId : {}", user.getUsername());

        return ResponseEntity.ok(null);
    }
}
