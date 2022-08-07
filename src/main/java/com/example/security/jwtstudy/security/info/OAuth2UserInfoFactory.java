package com.example.security.jwtstudy.security.info;

import com.example.security.jwtstudy.domain.user.entity.OAuth2Provider;

import java.util.Map;

// Provider 와 일치하는 OAuth2UserInfo 를 생성하고 반환한다.
public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(
            OAuth2Provider provider,
            Map<String, Object> attributes
    ) {
        // provider 추가
        switch (provider) {
            case KAKAO: return new KakaoUserInfo(attributes);
            default: throw new IllegalArgumentException("사용할 수 없는 Provider 입니다.");
        }
    }
}
