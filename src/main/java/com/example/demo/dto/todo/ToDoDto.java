package com.example.demo.dto.todo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ToDoDto {
    private Long id;
    private String title;
    private Boolean completed;
}
