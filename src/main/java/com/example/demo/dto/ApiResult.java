package com.example.demo.dto;

import com.example.demo.provider.MessageProvider;
import lombok.Getter;
import lombok.Setter;

/**
 * API 응답 DTO
 * <p>
 * 제네릭 타입 T를 사용하여 다양한 데이터 타입의 응답 내용을 포함할 수 있습니다.
 * 응답의 결과와 메시지를 통일된 형식으로 전달합니다.
 */
@Getter
@Setter
public class ApiResult<T> {
    private boolean result;   // 성공 여부
    private String message;   // 응답 메시지
    private T contents;       // 응답 데이터

    public ApiResult(boolean result, String message, T contents) {
        this.result = result;
        this.message = message;
        this.contents = contents;
    }

    // --- Static Factory Methods for Standardized Responses ---

    /**
     * 성공 응답 생성 (기본 성공 메시지 사용)
     *
     * @param contents 응답 데이터
     * @param <T>      응답 데이터 타입
     * @return ApiResult 인스턴스
     */
    public static <T> ApiResult<T> success(T contents) {
        return new ApiResult<>(true, MessageProvider.getMessage("common.success"), contents);
    }

    /**
     * 성공 응답 생성 (사용자 지정 메시지)
     *
     * @param contents 응답 데이터
     * @param message  사용자 지정 메시지
     * @param <T>      응답 데이터 타입
     * @return ApiResult 인스턴스
     */
    public static <T> ApiResult<T> success(T contents, String message) {
        return new ApiResult<>(true, message, contents);
    }

    /**
     * 실패 응답 생성
     *
     * @param message 사용자 지정 실패 메시지
     * @param <T>     응답 데이터 타입 (일반적으로 null)
     * @return ApiResult 인스턴스
     */
    public static <T> ApiResult<T> failure(String message) {
        return new ApiResult<>(false, message, null);
    }
}
