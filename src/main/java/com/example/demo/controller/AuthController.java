package com.example.demo.controller;

import com.example.demo.dto.ApiResult;
import com.example.demo.dto.sign.in.SignInRequest;
import com.example.demo.dto.sign.in.SignInResponse;
import com.example.demo.dto.sign.up.SignUpRequest;
import com.example.demo.dto.sign.up.SignUpResponse;
import com.example.demo.dto.user.UserDTO;
import com.example.demo.provider.JwtProvider;
import com.example.demo.provider.MessageProvider;
import com.example.demo.provider.TokenBlacklist;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final TokenBlacklist tokenBlacklist;

    /**
     * 사용자 로그인 (JWT 발급)
     *
     * @param signInRequest 로그인 요청 (이메일 & 비밀번호)
     * @return JWT 토큰 및 성공/실패 응답
     */
    @PostMapping("/signIn")
    public ResponseEntity<ApiResult<SignInResponse>> signIn(@RequestBody @Valid SignInRequest signInRequest) {
        return userService.authenticate(signInRequest.getEmail(), signInRequest.getPassword())
                .map(user -> {
                    String token = jwtProvider.generateToken(user.getEmail());
                    SignInResponse signInResponse = SignInResponse.builder()
                            .token(token).build();

                    return ResponseEntity.ok(new ApiResult<>(
                            true,
                            MessageProvider.getMessage("common.success"),
                            signInResponse
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResult<>(
                        false,
                        MessageProvider.getMessage("user.email.password.incorrect"),
                        SignInResponse.builder().build()
                )));
    }

    /**
     * 사용자 회원가입
     *
     * @param signUpRequest 회원가입 요청 데이터 (이메일 & 비밀번호)
     * @return 회원가입 성공 여부 응답
     */
    @PostMapping("/signUp")
    public ResponseEntity<ApiResult<SignUpResponse>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResult<>(
                            false,
                            MessageProvider.getMessage("user.email.exists"),
                            null
                    )
            );
        }

        UserDTO user = userService.save(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResult<>(
                        true,
                        MessageProvider.getMessage("common.success"),
                        SignUpResponse.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .build()
                )
        );
    }

    /**
     * 로그아웃 (JWT 블랙리스트 처리)
     *
     * @param request 클라이언트의 HTTP 요청
     * @return 로그아웃 성공/실패 여부 응답
     */
    @GetMapping("/signOut")
    public ResponseEntity<ApiResult<Object>> signOut(HttpServletRequest request) {
        // 요청에서 토큰 추출 후 유효성 검증
        Optional<String> tokenOptional = jwtProvider.extractToken(request)
                .filter(jwtProvider::validateToken);

        // 토큰이 유효하면 블랙리스트에 추가
        if (tokenOptional.isPresent()) {
            String token = tokenOptional.get();
            tokenBlacklist.addToBlacklist(token); // 블랙리스트에 추가

            return ResponseEntity.ok(new ApiResult<>(
                    true,
                    MessageProvider.getMessage("auth.logout.success"),
                    null
            ));
        }

        // 잘못된 토큰이거나 존재하지 않는 경우 처리
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiResult<>(
                        false,
                        MessageProvider.getMessage("auth.token.invalid"),
                        null
                )
        );
    }

}
