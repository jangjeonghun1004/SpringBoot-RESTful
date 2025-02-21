package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 요청한 리소스(게시글)가 존재하지 않을 경우 발생하는 예외.
 * @ResponseStatus 어노테이션을 사용하여 HTTP 404 상태 코드를 반환합니다.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
