package com.example.security.jwtstudy.security.jwt;

import com.example.security.jwtstudy.domain.refreshtoken.service.RefreshTokenService;
import com.example.security.jwtstudy.security.CustomAuthentication;
import com.example.security.jwtstudy.domain.refreshtoken.entity.RefreshToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token.expire-time}")
    private long accessTokenExpireTime;

    @Value("${jwt.refresh-token.expire-time}")
    private long refreshTokenExpireTime;

    private final RefreshTokenService refreshTokenService;

    public boolean validate(String jwtToken) {
        try {
            return this.getTokenClaims(jwtToken) != null;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature");
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token");
            throw e;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
            throw e;
        }
//        return false;
    }

    public Claims getTokenClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public Map<String, String> createTokens(Authentication authentication) {
        HashMap<String, String> tokens = new HashMap<>();
        String accessToken = createAccessToken(authentication);
        String refreshToken = createRefreshToken(authentication);
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public String createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(accessTokenExpireTime);

        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setIssuer("example")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(refreshTokenExpireTime);

        String token = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .setIssuer("example")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .compact();

        CustomAuthentication customAuthentication = (CustomAuthentication) authentication;

        Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findByUserId(customAuthentication.getUser().getId());
        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.orElseThrow();
            refreshTokenService.update(refreshToken, token, expiryDate);

            return refreshToken.getToken();
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .user(customAuthentication.getUser())
                .token(token)
                .expiryDate(expiryDate)
                .build();

        return refreshTokenService.save(refreshToken).getToken();
    }

    public String reissueRefreshToken(Authentication authentication, RefreshToken refreshToken) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(refreshTokenExpireTime);

        String token = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .setIssuer("example")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .compact();

        refreshTokenService.update(refreshToken, token, expiryDate);
        return token;
    }
}
