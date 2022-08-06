package com.example.security.jwtstudy.web.exception.advice;

import com.example.security.jwtstudy.exception.jwt.InvalidTokenException;
import com.example.security.jwtstudy.exception.jwt.NotExpiredTokenException;
import com.example.security.jwtstudy.web.exception.dto.ErrorDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(
        annotations = RestController.class,
        basePackages = "com.example.security.jwtstudy.web.auth.controller"
)
public class AuthExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<?> notExpiredTokenExceptionHandle(NotExpiredTokenException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<?> invalidTokenExceptionHandle(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<?> expiredJwtExceptionHandle(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto("토큰 만료"));
    }

    @ExceptionHandler
    public ResponseEntity<?> jwtExceptionHandle(JwtException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto("유효하지 않은 토큰"));
    }
}
