package com.example.security.config;

import com.example.security.domain.user.entity.User;
import com.example.security.domain.user.repository.UserRepository;
import com.example.security.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Start loadUserByUsername");
        log.info("username : {}", username);
        User user = userRepository.findByEmail(username).orElseThrow();

        log.info("user.password : {}", user.getPassword());

        log.info("End loadUserByUsername");

        return new JwtAuthentication(user);
    }
}
