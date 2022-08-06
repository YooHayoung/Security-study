package com.example.security.jwtstudy.exception.jwt;

import io.jsonwebtoken.JwtException;

public class InvalidTokenException extends JwtException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
