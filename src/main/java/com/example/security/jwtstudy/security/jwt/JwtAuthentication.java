package com.example.security.jwtstudy.security.jwt;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class JwtAuthentication extends User {

    private com.example.security.jwtstudy.domain.user.entity.User user;
    public JwtAuthentication(com.example.security.jwtstudy.domain.user.entity.User user) {
        super(user.getId().toString(),
                "",
                Arrays.stream(new String[]{user.getRole().name()})
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
        this.user = user;
    }
}
