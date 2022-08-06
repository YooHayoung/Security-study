package com.example.security.jwtstudy.config;

import com.example.security.jwtstudy.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

@Getter
public class CustomUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public CustomUser(User user) {
        super(user.getEmail(), user.getPassword(), Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())));
        this.user = user;
    }
}
