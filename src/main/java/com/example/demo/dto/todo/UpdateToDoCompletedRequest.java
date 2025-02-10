package com.example.demo.dto.todo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateToDoCompletedRequest {
    @Positive
    private Long id;

    @NotNull
    private Boolean completed;
}
