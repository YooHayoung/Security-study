package com.example.security.config;

import com.example.security.domain.user.entity.Address;
import com.example.security.domain.user.entity.Role;
import com.example.security.domain.user.entity.User;
import com.example.security.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TestConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void dataInit() {
        userRepository.save(User.builder()
                .email("user1@email.com")
                .password(passwordEncoder.encode("password1"))
                .address(new Address("city1", "street1", "detail1"))
                .name("user1")
                .birth(LocalDate.now())
                .role(Role.USER)
                .build());

        userRepository.save(User.builder()
                .email("user2@email.com")
                .password("password2")
                .address(new Address("city2", "street2", "detail2"))
                .name("user2")
                .birth(LocalDate.now())
                .role(Role.USER)
                .build());

        userRepository.save(User.builder()
                .email("admin1@email.com")
                .password("admin1")
                .address(new Address("city3", "street2", "detail3"))
                .name("admin1")
                .birth(LocalDate.now())
                .role(Role.ADMIN)
                .build());
    }
}
