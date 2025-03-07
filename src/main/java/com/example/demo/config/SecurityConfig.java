package com.example.demo.config;

import com.example.demo.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 보안 설정 REST API 컨트롤러
 * <p>
 * JWT 기반 인증, CORS 설정, 세션 정책 등을 구성하여 애플리케이션의 보안을 강화합니다.
 * <p>
 * 적용 URL:
 * <ul>
 *   <li>POST /api/auth/signIn : 인증 없이 로그인 요청 허용</li>
 *   <li>POST /api/auth/signUp : 인증 없이 회원가입 요청 허용</li>
 *   <li>GET /api/auth/signOut : 인증 없이 로그아웃 요청 허용</li>
 *   <li>그 외의 모든 요청 : 인증 필요</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    /**
     * SecurityFilterChain 빈 생성
     * <p>
     * URL 적용 내역:
     * <ul>
     *   <li>POST /api/auth/signIn, POST /api/auth/signUp, GET /api/auth/signOut : permitAll()</li>
     *   <li>나머지 모든 요청 : 인증 필요</li>
     * </ul>
     *
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain 빈
     * @throws Exception 보안 설정 구성 중 예외 발생 시
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 기본 HTTP 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // CSRF 보호 비활성화 (JWT 인증 사용 시 일반적으로 비활성화)
                .csrf(AbstractHttpConfigurer::disable)
                // CORS 설정 적용 (corsConfigurationSource 빈 사용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션을 사용하지 않음 (STATELESS 정책 적용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // URL 접근 제어 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/signIn").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signUp").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/signOut").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/post").permitAll()
                        .anyRequest().authenticated()
                )
                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 예외 처리 설정
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    /**
     * CORS 정책을 설정하는 빈
     * <p>
     * 적용 URL: 모든 경로 (/**)
     * <p>
     * 허용할 Origin, HTTP 메서드, Header 및 자격 증명(Credentials) 설정
     *
     * @return 구성된 CorsConfigurationSource 빈
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 쿠키 및 인증 정보 허용
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:8080",
                "https://jangjeonghun1004.github.io"
        )); // 허용할 프론트엔드 도메인
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type")); // 허용할 Header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정 적용
        return source;
    }
}
