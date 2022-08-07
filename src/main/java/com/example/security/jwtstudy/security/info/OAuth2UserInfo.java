package com.example.security.jwtstudy.security.info;

import com.example.security.jwtstudy.domain.user.entity.OAuth2Provider;

import java.util.Map;

// Provider 마다 사용자 정보를 내려줄때 이름이 조금씩 다르다.
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImgUrl();

    public abstract OAuth2Provider getProvider();
}
