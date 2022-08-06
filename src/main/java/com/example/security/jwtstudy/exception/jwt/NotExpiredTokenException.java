package com.example.security.jwtstudy.exception.jwt;

public class NotExpiredTokenException extends RuntimeException {

    public NotExpiredTokenException() {
        super();
    }

    public NotExpiredTokenException(String message) {
        super(message);
    }

    public NotExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExpiredTokenException(Throwable cause) {
        super(cause);
    }
}
