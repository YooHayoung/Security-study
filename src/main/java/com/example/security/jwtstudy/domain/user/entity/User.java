package com.example.security.jwtstudy.domain.user.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@ToString
@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String oauthId;

    private String email;

    private String name;

    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String oauthId, String email, String name, String profileImgUrl, OAuth2Provider provider) {
        this.oauthId = oauthId;
        this.email = email;
        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.provider = provider;
        this.role = Role.USER;
    }

    public void updateEmail(String email) {
        if (!email.equals(this.email)) {
            this.email = email;
        }
    }

    public void updateName(String name) {
        if (!name.equals(this.name)) {
            this.name = name;
        }
    }

    public void updateProfileImgUrl(String profileImgUrl) {
        if (!profileImgUrl.equals(this.profileImgUrl)) {
            this.profileImgUrl = profileImgUrl;
        }
    }

    public void updateUserInfo(String name, String email, String profileImgUrl) {
        updateName(name);
        updateEmail(email);
        updateProfileImgUrl(profileImgUrl);
    }
}
