package com.example.demo.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * JWT(JSON Web Token) 토큰의 생성, 검증, 파싱을 담당하는 컴포넌트입니다.
 * 이 클래스는 Spring Security 인증 체계에서 사용되며, 토큰 기반 인증을 구현합니다.
 *
 * <p>주요 기능:
 * <ul>
 *     <li>JWT 토큰 생성</li>
 *     <li>토큰 유효성 검증</li>
 *     <li>토큰에서 사용자 정보 추출</li>
 *     <li>HTTP 요청에서 토큰 추출</li>
 * </ul>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Component
public class JwtProvider {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int MINIMUM_SECRET_KEY_LENGTH = 32;

    private final SecretKey secretKey;
    private final long expirationMillis;

    /**
     * JWT Provider 인스턴스를 초기화합니다.
     *
     * @param secret JWT 서명에 사용될 비밀 키 (최소 32바이트)
     * @param expirationMillis 토큰 만료 시간 (밀리초)
     * @throws IllegalArgumentException 비밀 키가 최소 길이 요구사항을 충족하지 않는 경우
     */
    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expirationMillis}") long expirationMillis
    ) {
        validateSecretKey(secret);
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    /**
     * 사용자 식별자를 기반으로 새로운 JWT 토큰을 생성합니다.
     *
     * @param subject 사용자 식별자 (예: 이메일 또는 사용자 ID)
     * @return 생성된 JWT 토큰
     * @throws IllegalArgumentException subject가 null이거나 비어있는 경우
     */
    public String generateToken(String subject) {
        validateSubject(subject);

        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plusMillis(expirationMillis));

        return Jwts.builder()
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 식별자를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 식별자. 토큰이 유효하지 않은 경우 null
     */
    public String extractUsername(String token) {
        try {
            return parseToken(token).getSubject();
        } catch (JwtException e) {
            log.warn("JWT 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * HTTP 요청 헤더에서 Bearer 토큰을 추출합니다.
     *
     * @param request HTTP 요청
     * @return Bearer 토큰을 포함한 Optional
     */
    public Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 형식: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("잘못된 JWT 형식: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            log.warn("JWT 보안 검증 실패: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰의 만료 여부를 확인합니다.
     *
     * @param token JWT 토큰
     * @return 토큰이 만료된 경우 true, 유효한 경우 false
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = parseToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            log.warn("토큰 만료 확인 실패: {}", e.getMessage());
            return true;
        }
    }

    /**
     * JWT 토큰을 파싱하여 Claims를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 토큰의 Claims
     * @throws JwtException 토큰 파싱 실패 시
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void validateSecretKey(String secret) {
        if (secret == null || secret.length() < MINIMUM_SECRET_KEY_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("JWT Secret Key는 최소 %d바이트 이상이어야 합니다.", MINIMUM_SECRET_KEY_LENGTH)
            );
        }
    }

    private void validateSubject(String subject) {
        if (!StringUtils.hasText(subject)) {
            throw new IllegalArgumentException("Subject는 null이거나 비어있을 수 없습니다.");
        }
    }
}