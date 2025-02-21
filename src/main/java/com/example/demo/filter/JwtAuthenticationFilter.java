package com.example.demo.filter;

import com.example.demo.dto.ApiResult;
import com.example.demo.entity.Member;
import com.example.demo.provider.JwtProvider;
import com.example.demo.provider.TokenBlacklist;
import com.example.demo.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberService memberService;
    private final TokenBlacklist tokenBlacklist;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, MemberService memberService, TokenBlacklist tokenBlacklist) {
        this.jwtProvider = jwtProvider;
        this.memberService = memberService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // 요청에서 JWT 토큰을 추출
            Optional<String> tokenOptional = jwtProvider.extractToken(request);

            if (tokenOptional.isEmpty()) {
                log.debug("No JWT token found in request");
                filterChain.doFilter(request, response);
                return;
            }

            String token = tokenOptional.get();

            // JWT가 블랙리스트에 등록되어 있는지 확인
            if (tokenBlacklist.isBlacklisted(token)) {
                log.warn("Attempted use of blacklisted token: {}", token);
                handleException(response, "Token is blacklisted", HttpStatus.UNAUTHORIZED);
                return;
            }

            // JWT 유효성 검사
            if (!jwtProvider.validateToken(token)) {
                log.warn("Invalid or expired token: {}", token);
                handleException(response, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                return;
            }

            // 사용자 이메일 추출 후 데이터베이스 조회
            String email = jwtProvider.extractUsername(token);
            Optional<Member> userOptional = memberService.findMemberByEmail(email);

            if (userOptional.isEmpty()) {
                log.warn("User not found for email: {}", email);
                handleException(response, "User not found", HttpStatus.NOT_FOUND);
                return;
            }

            // Spring Security에 사용자 인증 정보 설정
            setAuthentication(userOptional.get());
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Authentication error", ex);
            handleException(response, "Authentication error: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Spring Security에 인증 정보 설정
     *
     * @param member 인증할 사용자 객체
     */
    private void setAuthentication(Member member) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                member,
                null,
                member.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User authenticated: {}", member.getEmail());
    }

    /**
     * 예외 발생 시 클라이언트 응답을 처리하는 메서드
     *
     * @param response HTTP 응답 객체
     * @param message  오류 메시지
     * @param status   HTTP 상태 코드
     * @throws IOException JSON 변환 및 응답 전송 예외 처리
     */
    private void handleException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        log.error("Authentication error response: {} - {}", status, message);
        ApiResult<Void> apiResult = new ApiResult<>(false, message, null);
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResult));
    }
}
