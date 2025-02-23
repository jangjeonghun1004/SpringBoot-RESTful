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

/**
 * JWT 인증 필터
 * <p>
 * 모든 HTTP 요청에서 JWT 토큰을 추출하여 검증하고, 유효한 경우 Spring Security 컨텍스트에 인증 정보를 설정합니다.
 * <p>
 * 다음 URL은 토큰 검증을 건너뜁니다:
 * <ul>
 *   <li>POST /api/auth/signIn</li>
 *   <li>GET /api/auth/signOut</li>
 * </ul>
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberService memberService;
    private final TokenBlacklist tokenBlacklist;

    /**
     * 생성자 - JwtProvider, MemberService, TokenBlacklist를 주입받습니다.
     *
     * @param jwtProvider    JWT 토큰 관련 기능 제공
     * @param memberService  회원 정보 조회 서비스
     * @param tokenBlacklist 블랙리스트 토큰 관리 서비스
     */
    public JwtAuthenticationFilter(JwtProvider jwtProvider, MemberService memberService, TokenBlacklist tokenBlacklist) {
        this.jwtProvider = jwtProvider;
        this.memberService = memberService;
        this.tokenBlacklist = tokenBlacklist;
    }

    /**
     * HTTP 요청에 대해 JWT 토큰을 검증하고, 인증 정보를 설정하는 필터 체인을 수행합니다.
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외 발생 시
     * @throws IOException      입출력 예외 발생 시
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (shouldSkipTokenValidation(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            Optional<String> tokenOptional = jwtProvider.extractToken(request);
            if (tokenOptional.isEmpty()) {
                log.debug("No JWT token found in request");
                filterChain.doFilter(request, response);
                return;
            }

            String token = tokenOptional.get();
            if (tokenBlacklist.isBlacklisted(token)) {
                log.warn("Attempted use of blacklisted token: {}", token);
                handleException(response, "Token is blacklisted", HttpStatus.UNAUTHORIZED);
                return;
            }

            if (!jwtProvider.validateToken(token)) {
                log.warn("Invalid or expired token: {}", token);
                handleException(response, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                return;
            }

            String email = jwtProvider.extractUsername(token);
            Optional<Member> memberOptional = memberService.findMemberByEmail(email);
            if (memberOptional.isEmpty()) {
                log.warn("User not found for email: {}", email);
                handleException(response, "User not found", HttpStatus.NOT_FOUND);
                return;
            }

            setAuthentication(memberOptional.get());
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Authentication error", ex);
            handleException(response, "Authentication error: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 요청 URI가 토큰 검증을 건너뛰어야 하는지 여부를 판단합니다.
     *
     * @param request HTTP 요청 객체
     * @return 검증을 건너뛰어야 하면 true, 그렇지 않으면 false
     */
    private boolean shouldSkipTokenValidation(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.equals("/api/auth/signIn") || requestURI.equals("/api/auth/signOut");
    }

    /**
     * Spring Security에 인증 정보를 설정합니다.
     *
     * @param member 인증할 회원 객체
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
     * 예외 발생 시 클라이언트에 JSON 형식의 에러 응답을 전송합니다.
     *
     * @param response HTTP 응답 객체
     * @param message  오류 메시지
     * @param status   HTTP 상태 코드
     * @throws IOException JSON 변환 및 응답 전송 중 발생하는 예외
     */
    private void handleException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        log.error("Authentication error response: {} - {}", status, message);
        ApiResult<Void> apiResult = ApiResult.failure(message);
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResult));
    }
}
