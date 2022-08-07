package com.example.security.jwtstudy.domain.user.entity;

import lombok.Getter;


@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    GUEST("GUEST"),
    ;

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getRoleValue() {
        return value;
    }
}
