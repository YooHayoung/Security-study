package com.example.security.jwt;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;
import java.util.stream.Collectors;

public class JwtAuthentication extends User {
    public JwtAuthentication(com.example.security.domain.user.entity.User user) {
        super(user.getId().toString(),
                user.getPassword(),
                Arrays.stream(new String[]{user.getRole().name()})
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
}
