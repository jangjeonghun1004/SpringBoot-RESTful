package com.example.demo.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

/**
 * JWT(Json Web Token)를 생성, 검증 및 파싱하는 유틸리티 클래스
 */
@Slf4j
@Component
public class JwtProvider {
    private final SecretKey secretKey;
    private final long expirationMillis;

    /**
     * 생성자 - JWT 서명용 SecretKey 및 토큰 만료 시간 초기화
     *
     * @param secret           JWT 서명을 위한 비밀 키
     * @param expirationMillis JWT 만료 시간 (밀리초 단위)
     */
    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expirationMillis}") long expirationMillis) {
        if (secret.length() < 32) {  // 최소 256비트(32바이트) 보장
            throw new IllegalArgumentException("JWT Secret Key는 최소 32바이트 이상이어야 합니다.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    /**
     * JWT 토큰 생성
     *
     * @param subject 사용자 ID 또는 이메일 등
     * @return 서명된 JWT 문자열
     */
    public String generateToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT에서 사용자 이름(Subject) 추출
     *
     * @param token JWT 토큰
     * @return 사용자 식별자 (이메일 또는 ID)
     */
    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("JWT가 만료되었습니다: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     *
     * @param request HTTP 요청 객체
     * @return Bearer 토큰 (옵셔널)
     */
    public Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return Optional.of(header.substring(7));
        }
        return Optional.empty();
    }

    /**
     * JWT 유효성 검증
     *
     * @param token JWT 토큰
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 형식입니다: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT 서명이 유효하지 않습니다: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
        }
        return false;
    }

    /**
     * JWT 만료 여부 확인
     *
     * @param token JWT 토큰
     * @return 만료된 경우 true, 그렇지 않으면 false
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.warn("JWT 만료 확인 중 오류 발생: {}", e.getMessage());
            return true; // 만료 여부 확인 실패 시 안전하게 처리
        }
    }

    /**
     * JWT에서 만료 시간 추출
     *
     * @param token JWT 토큰
     * @return 토큰 만료 시간
     */
    private Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }
}
