package com.example.security.jwtstudy.config;

import com.example.security.jwtstudy.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthentication userDetails = (JwtAuthentication) userDetailsService.loadUserByUsername(authentication.getName());

        if (!passwordMatches(authentication, userDetails)) {
            throw new RuntimeException("인증 실패");
        }

        return new CustomAuthentication(userDetails.getUser());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private boolean passwordMatches(Authentication authentication, UserDetails userDetails) {
        return passwordEncoder.matches(
                authentication.getCredentials().toString(),
                userDetails.getPassword());
    }
}
