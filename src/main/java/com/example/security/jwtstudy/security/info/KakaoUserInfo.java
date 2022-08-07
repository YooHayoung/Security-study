package com.example.security.jwtstudy.security.info;

import com.example.security.jwtstudy.domain.user.entity.OAuth2Provider;

import java.util.Map;

public class KakaoUserInfo extends OAuth2UserInfo {

    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return getProperty("nickname");
    }

    @Override
    public String getEmail() {
        return getKakaoAccountEmail();
    }

    @Override
    public String getImgUrl() {
        return getProperty("thumbnail_image");
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.KAKAO;
    }

    private String getProperty(String propertyName) {
        Map<String, Object> properties =
                (Map<String, Object>) attributes.get("properties");

        if (properties == null) {
            return null;
        }

        return (String) properties.get(propertyName);
    }

    private String getKakaoAccountEmail() {
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) {
            return null;
        }

        return (String) kakaoAccount.get("email");
    }
}
