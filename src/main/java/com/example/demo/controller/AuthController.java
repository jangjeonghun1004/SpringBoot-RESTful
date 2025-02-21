package com.example.demo.controller;

import com.example.demo.dto.ApiResult;
import com.example.demo.dto.member.MemberResponse;
import com.example.demo.dto.sign.in.SignInRequest;
import com.example.demo.dto.sign.in.SignInResponse;
import com.example.demo.dto.sign.up.SignUpRequest;
import com.example.demo.dto.sign.up.SignUpResponse;
import com.example.demo.provider.JwtProvider;
import com.example.demo.provider.MessageProvider;
import com.example.demo.provider.TokenBlacklist;
import com.example.demo.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 인증 REST API 컨트롤러
 *
 * 회원가입, 로그인, 로그아웃 기능을 제공하며,
 * JWT 기반 인증 및 블랙리스트 처리를 수행합니다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final TokenBlacklist tokenBlacklist;

    /**
     * 사용자 로그인 API (JWT 발급)
     * URL: POST /api/auth/signIn
     *
     * @param signInRequest 이메일 및 비밀번호를 포함한 로그인 요청 데이터
     * @return JWT 토큰 및 응답 메시지를 포함한 ApiResult
     * @status 200 OK - 로그인 성공 (JWT 발급)
     * @status 401 Unauthorized - 로그인 실패 (이메일 또는 비밀번호 불일치)
     */
    @PostMapping("/signIn")
    public ResponseEntity<ApiResult<SignInResponse>> signIn(@RequestBody @Valid SignInRequest signInRequest) {
        return memberService.authenticateMember(signInRequest.getEmail(), signInRequest.getPassword())
                .map(user -> {
                    String token = jwtProvider.generateToken(user.getEmail());
                    SignInResponse response = SignInResponse.builder().token(token).build();
                    return ResponseEntity.ok(ApiResult.success(response));
                })
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResult.failure(MessageProvider.getMessage("user.email.password.incorrect")))
                );
    }

    /**
     * 사용자 회원가입 API
     * URL: POST /api/auth/signUp
     *
     * @param signUpRequest 회원가입 요청 데이터 (이메일, 비밀번호 등)
     * @return 회원가입 성공 메시지 및 등록된 사용자 정보
     * @status 201 Created - 회원가입 성공
     * @status 400 Bad Request - 유효성 검사 실패
     */
    @PostMapping("/signUp")
    public ResponseEntity<ApiResult<SignUpResponse>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        MemberResponse user = memberService.saveMember(signUpRequest);
        SignUpResponse response = SignUpResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.success(response));
    }

    /**
     * 사용자 로그아웃 API (JWT 블랙리스트 처리)
     * URL: GET /api/auth/signOut
     *
     * @param request 클라이언트의 HTTP 요청 (Authorization 헤더에 JWT 포함)
     * @return 로그아웃 성공 여부 응답
     * @status 200 OK - 로그아웃 성공 (토큰 블랙리스트 등록됨)
     * @status 401 Unauthorized - 잘못된 또는 누락된 토큰
     */
    @GetMapping("/signOut")
    public ResponseEntity<ApiResult<Object>> signOut(HttpServletRequest request) {
        return extractValidToken(request)
                .map(token -> {
                    tokenBlacklist.addToBlacklist(token);
                    return ResponseEntity.ok(ApiResult.success(null, MessageProvider.getMessage("auth.logout.success")));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResult.failure(MessageProvider.getMessage("auth.token.invalid"))));
    }

    /**
     * 요청에서 JWT 토큰을 추출하고, 유효성을 검사하여 반환하는 헬퍼 메서드
     *
     * @param request 클라이언트의 HTTP 요청
     * @return 유효한 JWT 토큰 (Optional)
     */
    private Optional<String> extractValidToken(HttpServletRequest request) {
        return jwtProvider.extractToken(request)
                .filter(jwtProvider::validateToken);
    }
}
