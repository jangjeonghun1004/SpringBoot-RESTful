package com.example.demo.exception;

import com.example.demo.dto.ApiResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리 클래스
 * <p>
 * 컨트롤러 및 보안 필터에서 발생하는 예외를 일괄 처리하여 클라이언트에 일관된 응답(ApiResult 형식)을 제공합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 예외 발생 시 로그를 남기기 위한 로거
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * HttpRequestMethodNotSupportedException 처리
     * <p>
     * 클라이언트가 지원되지 않는 HTTP 메서드로 요청을 보낸 경우 발생하는 예외를 처리하며,
     * HTTP 405 Method Not Allowed 상태와 함께 에러 메시지를 반환합니다.
     *
     * @param ex 발생한 HttpRequestMethodNotSupportedException
     * @return 405 Method Not Allowed 상태와 에러 메시지를 포함한 ApiResult<Void> 응답
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("HTTP method '%s' is not supported for this request. Supported methods: %s",
                ex.getMethod(),
                ex.getSupportedHttpMethods());
        logger.error("HTTP request method not supported: {}", message, ex);
        return buildErrorResponse(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * NoResourceFoundException 처리
     * <p>
     * 요청한 리소스를 찾을 수 없는 경우 발생하는 예외를 처리하며,
     * 클라이언트에 HTTP 404 Not Found 상태와 함께 에러 메시지를 반환합니다.
     *
     * @param ex 발생한 NoResourceFoundException
     * @return 404 Not Found 상태와 에러 메시지를 포함한 ApiResult<Void> 응답
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResult<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        logger.error("No resource found: ", ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * 에러 응답 생성을 위한 헬퍼 메서드
     *
     * @param message 에러 메시지
     * @param status  HTTP 상태 코드
     * @return ResponseEntity<ApiResult<Void>>
     */
    private ResponseEntity<ApiResult<Void>> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResult.failure(message));
    }

    /**
     * 사용자를 찾을 수 없는 경우 (UsernameNotFoundException)
     *
     * @param ex UsernameNotFoundException 예외
     * @return 404 Not Found 응답
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResult<Void>> handleUserNotFoundException(UsernameNotFoundException ex) {
        logger.error("User not found: ", ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * 유효성 검사 실패 (MethodArgumentNotValidException)
     *
     * @param ex MethodArgumentNotValidException 예외
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        logger.error("Validation error: {}", errorMessage, ex);
        return buildErrorResponse(errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * 요청 파라미터 누락 (MissingServletRequestParameterException)
     *
     * @param ex MissingServletRequestParameterException 예외
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResult<Void>> handleMissingParameterException(MissingServletRequestParameterException ex) {
        String message = "Missing parameter: " + ex.getParameterName();
        logger.error("Missing request parameter: {}", ex.getParameterName(), ex);
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 접근 거부 (AccessDeniedException)
     *
     * @param ex AccessDeniedException 예외
     * @return 403 Forbidden 응답
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResult<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        String message = "Access Denied: " + ex.getMessage();
        logger.error("Access denied: ", ex);
        return buildErrorResponse(message, HttpStatus.FORBIDDEN);
    }

    /**
     * 인증 실패 시 처리 (401 Unauthorized)
     * <p>
     * Spring Security에서 발생하는 AuthenticationException 계열의 예외를 처리합니다.
     *
     * @param ex AuthenticationException 예외
     * @return 401 Unauthorized 응답
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResult<Void>> handleAuthenticationException(AuthenticationException ex) {
        String message = "Unauthorized: " + ex.getMessage();
        logger.error("Authentication exception occurred: ", ex);
        return buildErrorResponse(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * ConstraintViolationException 처리 (메서드 매개변수 유효성 검증 실패)
     *
     * @param ex ConstraintViolationException 예외
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        // 모든 제약 조건 위반 메시지를 콤마로 연결하여 하나의 문자열로 만듭니다.
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        logger.error("Constraint violation error: {}", errorMessage, ex);
        return buildErrorResponse(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResult<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(),
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");
        logger.error("Method argument type mismatch: {}", message, ex);
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StandardException.class)
    public ResponseEntity<ApiResult<Void>> handleStandardException(StandardException ex) {
        String message = ex.getMessage();
        logger.error("Standard exception: {}", message, ex);
        return buildErrorResponse(message, HttpStatus.I_AM_A_TEAPOT);
    }

    /**
     * 기타 모든 예외 처리 (Generic Exception)
     * <p>
     * 내부 오류 메시지 노출을 최소화하여 보안을 강화합니다.
     *
     * @param ex Exception 예외
     * @return 500 Internal Server Error 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: ", ex);
        String message = "An unexpected error occurred. Please contact support if the problem persists.";
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
