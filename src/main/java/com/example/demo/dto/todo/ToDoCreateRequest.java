package com.example.demo.dto.todo;

import com.example.demo.provider.MessageProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 할 일 생성 요청 DTO
 */
@Getter
@Setter
public class ToDoCreateRequest {

    @NotBlank(message = "title: {common.validation.notNull}")
    private String title;
}
