package com.example.security.jwtstudy.exception.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;

public class AccessTokenExpiredException extends ExpiredJwtException {

    public AccessTokenExpiredException(ExpiredJwtException e) {
        super(e.getHeader(), e.getClaims(), e.getMessage(), e.getCause());
    }

    public AccessTokenExpiredException(Header header, Claims claims, String message) {
        super(header, claims, message);
    }

    public AccessTokenExpiredException(Header header, Claims claims, String message, Throwable cause) {
        super(header, claims, message, cause);
    }
}
