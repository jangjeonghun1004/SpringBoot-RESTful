package com.example.demo.dto.todo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 할 일 완료 상태 변경 요청 DTO
 */
@Getter
@Setter
public class UpdateToDoCompletedRequest {

    @NotNull(message = "id: {common.validation.notNull}")
    private Long id;

    @NotNull(message = "completed: {common.validation.notNull}")
    private Boolean completed;
}
