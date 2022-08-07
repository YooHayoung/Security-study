package com.example.security.jwtstudy.config;

import com.example.security.jwtstudy.domain.refreshtoken.repository.RefreshTokenRepository;
import com.example.security.jwtstudy.security.oauth.service.CustomOAuth2UserService;
//import com.example.security.jwtstudy.security.oauth.service.CustomUserDetailsService;
import com.example.security.jwtstudy.security.jwt.filter.JwtAuthenticationExceptionFilter;
import com.example.security.jwtstudy.security.jwt.filter.JwtAuthenticationFilter;
import com.example.security.jwtstudy.security.jwt.provider.JwtTokenProvider;
import com.example.security.jwtstudy.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

//    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomOAuth2UserService oAuth2UserService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() {
//        return new JwtAuthenticationFilter(jwtTokenProvider);
//    }
//
//    @Bean
//    public JwtAuthenticationExceptionFilter jwtAuthenticationExceptionFilter() {
//        return new JwtAuthenticationExceptionFilter();
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                    .csrf().disable()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .cors().configurationSource(corsConfigurationSource())
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                    .antMatchers("/api/auth/login").permitAll()
                    .antMatchers("/api/auth/reissue/**").permitAll()
                    .antMatchers("/oauth/authorization/**").permitAll() // 인가 코드 넘겨받기
                    .anyRequest().authenticated();
//                .and()
//                    .oauth2Login()
//                    .authorizationEndpoint()
//                    .baseUri("/oauth/authorization")
//                    .authorizationRequestRepository(null)
//                .and()
//                    .redirectionEndpoint()
//                    .baseUri("/auth/code/*")
//                .and()
//                    .userInfoEndpoint()
//                    .userService(oAuth2UserService)
//                .and()
//                    .successHandler(null)
//                    .failureHandler(null);

//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.addFilterBefore(jwtAuthenticationExceptionFilter(), jwtAuthenticationFilter().getClass());

        return http.build();
    }

}
