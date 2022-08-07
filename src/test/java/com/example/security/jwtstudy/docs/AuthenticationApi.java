package com.example.security.jwtstudy.docs;

import com.example.security.RestDocsSupport;
import com.example.security.jwtstudy.domain.refreshtoken.entity.RefreshToken;
import com.example.security.jwtstudy.domain.refreshtoken.repository.RefreshTokenRepository;
import com.example.security.jwtstudy.domain.user.entity.User;
import com.example.security.jwtstudy.domain.user.repository.UserRepository;
import com.example.security.jwtstudy.security.jwt.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
class AuthenticationApi extends RestDocsSupport {

    @Autowired
    JwtTokenUtils jwtTokenUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void login() throws Exception {
        JSONObject json = new JSONObject();
        String loginId = "user1@email.com";
        String password = "password1";
        json.put("loginId", loginId);
        json.put("password", password);

        ResultActions perform = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        // refreshToken 저장 확인
        List<RefreshToken> all = refreshTokenRepository.findAll();
        for (RefreshToken refreshToken : all) {
            log.info("saved refreshToken : {}", refreshToken.getToken());
        }

        perform.andDo(
                restDocs.document(
                        requestFields(
                                fieldWithPath("loginId").type(JsonFieldType.STRING).description("아이디"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("Access Token"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("Refresh Token")
                        )
                )
        );
    }

    @Test
    void reissueAccessToken() throws Exception {
        User user = userRepository.findById(1L).orElseThrow();
        Map<String, String> tokens = jwtTokenUtils.createTokens(
                new UsernamePasswordAuthenticationToken(
                        user.getId(),
                        "",
                        Arrays.stream(new String[]{user.getRole().name()})
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                )
        );

        ResultActions perform = mockMvc.perform(
                post("/api/auth/reissue/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", tokens.get("accessToken"))
                        .header("RefreshToken", tokens.get("refreshToken"))
        );
    }
}