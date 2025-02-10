package com.example.demo.dto.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateToDoTitleRequest {
    @Positive
    private Long id;

    @NotBlank
    private String title;
}
