package com.example.demo.dto.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 할 일 제목 수정 요청 DTO
 */
@Getter
@Setter
public class UpdateToDoTitleRequest {

    @NotNull(message = "id: {common.validation.notNull}")
    private Long id;

    @NotBlank(message = "title: {common.validation.notNull}")
    private String title;
}
