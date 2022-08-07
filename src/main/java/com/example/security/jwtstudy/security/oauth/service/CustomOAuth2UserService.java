package com.example.security.jwtstudy.security.oauth.service;

import com.example.security.jwtstudy.domain.user.entity.User;
import com.example.security.jwtstudy.domain.user.repository.UserRepository;
import com.example.security.jwtstudy.domain.user.entity.OAuth2Provider;
import com.example.security.jwtstudy.security.info.OAuth2UserInfo;
import com.example.security.jwtstudy.security.info.OAuth2UserInfoFactory;
import com.example.security.jwtstudy.security.oauth.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    // Provider 로부터 사용자 정보를 받아오고, 등록 및 변경 사항을 수정한다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2Provider oAuth2Provider = OAuth2Provider.valueOf(
                userRequest.getClientRegistration()
                        .getRegistrationId()
                        .toUpperCase()
        );

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2Provider,
                oAuth2User.getAttributes()
        );

        Optional<User> findUser = userRepository.findByOauthId(oAuth2UserInfo.getId());

        User user = null;

        if (findUser.isPresent()) { // 이미 존재하는 사용자는 수정
            user = updateUser(findUser.orElseThrow(), oAuth2UserInfo);
        } else { // 존재하지 않으면 등록
            user = createUser(oAuth2Provider, oAuth2UserInfo);
        }

        // 인증 객체 생성 및 반환
        return new UserPrincipal(user, oAuth2User.getAttributes());
    }

    private User createUser(OAuth2Provider oAuth2Provider, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .oauthId(oAuth2UserInfo.getId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .profileImgUrl(oAuth2UserInfo.getImgUrl())
                .provider(oAuth2Provider)
                .build();
        return userRepository.save(user);
    }

    private User updateUser(User user, OAuth2UserInfo oAuth2UserInfo) {
        user.updateUserInfo(oAuth2UserInfo.getName(), oAuth2UserInfo.getEmail(), oAuth2UserInfo.getImgUrl());
        return user;
    }
}
