package com.example.security.web.dto;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String loginId;
    private String password;
}
