package com.example.security.jwtstudy.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String city;

    private String streetName;

    private String detail;

    public String fullAddress() {
        return city + ", " + streetName + ", (" + detail + ")";
    }
}
