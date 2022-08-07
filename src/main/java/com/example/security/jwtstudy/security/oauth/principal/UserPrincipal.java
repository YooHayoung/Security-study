package com.example.security.jwtstudy.security.oauth.principal;

import com.example.security.jwtstudy.domain.user.entity.OAuth2Provider;
import com.example.security.jwtstudy.domain.user.entity.Role;
import com.example.security.jwtstudy.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class UserPrincipal implements OAuth2User, UserDetails, Authentication {

    private final Long userId;
    private final OAuth2Provider provider;
    private final Role role;
    private final Collection<GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return userId.toString();
    }

    public UserPrincipal(User user) {
        this.userId = user.getId();
        this.provider = user.getProvider();
        this.role = user.getRole();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleValue()));
    }

    public UserPrincipal(User user, Map<String, Object> attributes) {
        this.userId = user.getId();
        this.provider = user.getProvider();
        this.role = user.getRole();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleValue()));
        this.attributes = attributes;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId.toString();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }
}
