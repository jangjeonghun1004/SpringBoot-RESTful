package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResult<T> {
    private boolean result;
    private String message;
    private T contents;

    public enum MessageType {
        SUCCESS, FAILURE, UNKNOWN_ERROR
    }

    public ApiResult(boolean result, String message, T contents) {
        this.result = result;
        this.message = message;
        this.contents = contents;
    }
}
