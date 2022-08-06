package com.example.security.jwtstudy.domain.user.entity;

public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    ;

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getRoleValue() {
        return value;
    }
}
