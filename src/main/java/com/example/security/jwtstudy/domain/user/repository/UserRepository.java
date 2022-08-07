package com.example.security.jwtstudy.domain.user.repository;

import com.example.security.jwtstudy.domain.user.entity.OAuth2Provider;
import com.example.security.jwtstudy.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByOauthId(String oauthId);

    Optional<User> findByOauthIdAndProvider(String oauthId, OAuth2Provider provider);
}
