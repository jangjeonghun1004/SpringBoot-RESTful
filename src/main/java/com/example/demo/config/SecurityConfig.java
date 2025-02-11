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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 기본 HTTP 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // CSRF 보호 비활성화 (JWT 인증 사용 시 일반적으로 비활성화)
                .csrf(AbstractHttpConfigurer::disable)
                // CORS 설정 적용 (Bean에서 설정한 corsConfigurationSource 사용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션을 사용하지 않음 (JWT 기반 인증이므로 STATELESS 정책 적용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // URL 접근 제어
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signIn").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signUp").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/signOut").permitAll()
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                // JWT 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 예외 처리 설정
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint) // 인증 실패 시 핸들러
                        .accessDeniedHandler(accessDeniedHandler) // 권한 부족 시 핸들러
                );

        return http.build();
    }

    /**
     * CORS 정책을 설정하는 Bean
     * - 허용할 Origin, HTTP Method, Header 설정
     * - Credentials(쿠키, 인증 정보) 허용 여부 지정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 쿠키 및 인증 정보 허용
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:8080", "https://jangjeonghun1004.github.io/")); // 허용할 도메인 (프론트엔드 도메인)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type")); // 허용할 헤더

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 적용
        return source;
    }
}
