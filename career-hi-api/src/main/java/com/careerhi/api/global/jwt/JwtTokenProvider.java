package com.careerhi.api.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets; // ★ 추가됨
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        // [수정 전] Base64 디코딩 시도 -> 에러 발생 원인
        // byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // this.key = Keys.hmacShaKeyFor(keyBytes);

        // [수정 후] 일반 문자열을 그대로 바이트로 변환하여 키로 사용
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 1. Access Token 생성
    public String createAccessToken(Long userId, String email) {
        long now = (new Date()).getTime();
        long validTime = 1000 * 60 * 60; // 1시간

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("auth", "ROLE_USER")
                .setExpiration(new Date(now + validTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Refresh Token 생성
    public String createRefreshToken() {
        long now = (new Date()).getTime();
        long validTime = 1000 * 60 * 60 * 24 * 14; // 14일

        return Jwts.builder()
                .setExpiration(new Date(now + validTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. 토큰에서 인증 정보(Authentication) 추출
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 4. 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // JwtTokenProvider.java 안에 추가
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 클래스 상단에 선언된 secret key 변수명 확인
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // 보통 subject에 email을 넣어둡니다.
    }
}