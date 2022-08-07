package com.example.security.jwtstudy.web.user.controller;

import com.example.security.jwtstudy.domain.user.entity.User;
import com.example.security.jwtstudy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // 사용자 정보 조회
    @GetMapping
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal Long userId) {

        User user = userService.findById(userId).orElseThrow();

        return ResponseEntity.ok().body(user);
    }
}
