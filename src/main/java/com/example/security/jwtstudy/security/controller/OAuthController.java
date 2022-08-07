package com.example.security.jwtstudy.security.controller;

import com.example.security.jwtstudy.domain.user.entity.User;
import com.example.security.jwtstudy.security.jwt.provider.JwtTokenProvider;
import com.example.security.jwtstudy.security.oauth.service.OAuth2Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuth2Service oAuth2Service;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/oauth/authorization/{provider}")
    public ResponseEntity<?> login(@PathVariable String provider,
                                   @RequestParam String code) throws JsonProcessingException {

        User user = oAuth2Service.login(provider, code);

        return ResponseEntity.ok(user);
    }
}
