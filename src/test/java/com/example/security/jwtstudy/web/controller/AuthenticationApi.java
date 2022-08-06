package com.example.security.jwtstudy.web.controller;

import com.example.security.RestDocsSupport;
import com.example.security.jwtstudy.domain.refreshtoken.entity.RefreshToken;
import com.example.security.jwtstudy.domain.refreshtoken.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
class AuthenticationApi extends RestDocsSupport {

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
                post("/api/login")
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

//    @Test
//    void login_fail() throws Exception {
//        String loginId = "user1@email.com";
//        String password = "password";
//        mockMvc.perform(
//                post("/api/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"loginId\":\"" + loginId + "\",\"password\":\"" + password + "\"}")
//        ).andExpect(status().isForbidden());
//    }
//
//    @Test
//    void hello() throws Exception {
//        String loginId = "user1@email.com";
//        String password = "password1";
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, password);
//
//        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//
//        String accessToken = jwtTokenUtils.createAccessToken(authenticate);
//
//        mockMvc.perform(
//                get("/api/hello")
//                        .header("Authorization", "Bearer " + accessToken)
//        ).andExpect(status().isOk());
//    }
}