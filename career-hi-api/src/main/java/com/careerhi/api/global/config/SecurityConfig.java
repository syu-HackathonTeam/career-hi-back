package com.careerhi.api.global.config;

import com.careerhi.api.global.jwt.JwtAuthenticationFilter;
import com.careerhi.api.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // 생성자 주입 추가
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider; // 주입 받음

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 로그인, 회원가입, H2 콘솔은 허용
                        .requestMatchers("/api/v1/auth/**", "/h2-console/**").permitAll()
                        // 그 외 모든 요청은 인증(토큰) 필요!
                        .anyRequest().authenticated()
                )
                // ★ JWT 필터 추가: UsernamePasswordAuthenticationFilter 앞에서 미리 검사
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}