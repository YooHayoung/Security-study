package com.example.security.jwtstudy.security.oauth.service;

import com.example.security.jwtstudy.domain.user.entity.OAuth2Provider;
import com.example.security.jwtstudy.domain.user.entity.User;
import com.example.security.jwtstudy.domain.user.repository.UserRepository;
import com.example.security.jwtstudy.security.info.OAuth2UserInfo;
import com.example.security.jwtstudy.security.info.OAuth2UserInfoFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2Service {

    private final InMemoryClientRegistrationRepository clientRegistrationRepository;
    private final UserRepository userRepository;

    public User login(String providerName, String code) throws JsonProcessingException {
        ClientRegistration provider = clientRegistrationRepository.findByRegistrationId(providerName);
        Map<String, Object> oAuthTokenInfo = getToken(code, provider);
        String oAuthAccessToken = (String) oAuthTokenInfo.get("access_token");
        return getUserInfo(oAuthAccessToken, provider);
    }

    private User getUserInfo(String oAuthAccessToken, ClientRegistration provider) {
        Map<String, Object> userAttributes = getUserAttributes(oAuthAccessToken, provider);

        log.info("userAttributes : {}", userAttributes);

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                OAuth2Provider.valueOf(provider.getRegistrationId().toUpperCase()),
                userAttributes
        );

        Optional<User> findUser = userRepository.findByOauthIdAndProvider(oAuth2UserInfo.getId(), oAuth2UserInfo.getProvider());

        User user = null;

        if (findUser.isPresent()) { // 이미 존재하는 사용자는 수정
            user = updateUser(findUser.orElseThrow(), oAuth2UserInfo);
        } else { // 존재하지 않으면 등록
            user = createUser(oAuth2UserInfo);
        }

        log.info("user : {}", user);

        return user;
    }

    private Map<String, Object> getUserAttributes(String oAuthAccessToken, ClientRegistration provider) {
        return WebClient.create()
                .post()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(oAuthAccessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    private Map<String, Object> getToken(String code, ClientRegistration provider) throws JsonProcessingException {
        return WebClient.create()
                .post()
                .uri(provider.getProviderDetails().getTokenUri())
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    httpHeaders.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenRequestParams(code, provider))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    private MultiValueMap<String, String> tokenRequestParams(String code, ClientRegistration provider) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", provider.getRedirectUri());
        params.add("client_id", provider.getClientId());
        params.add("client_secret", provider.getClientSecret());
        return params;
    }

    private User createUser(OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .oauthId(oAuth2UserInfo.getId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .profileImgUrl(oAuth2UserInfo.getImgUrl())
                .provider(oAuth2UserInfo.getProvider())
                .build();
        return userRepository.save(user);
    }

    private User updateUser(User user, OAuth2UserInfo oAuth2UserInfo) {
        user.updateUserInfo(oAuth2UserInfo.getName(), oAuth2UserInfo.getEmail(), oAuth2UserInfo.getImgUrl());
        return user;
    }
}
