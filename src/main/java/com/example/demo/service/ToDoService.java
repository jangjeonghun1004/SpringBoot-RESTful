package com.example.demo.service;

import com.example.demo.dto.todo.ToDoDto;

import java.util.List;

/**
 * 할 일(ToDo) 서비스 인터페이스
 * <p>
 * 할 일 목록 조회, 생성, 수정 및 삭제 기능을 제공합니다.
 */
public interface ToDoService {

    /**
     * 모든 할 일 목록을 조회합니다.
     *
     * @return 할 일 DTO 목록
     */
    List<ToDoDto> findAllTodos();

    /**
     * 새로운 할 일을 생성합니다.
     *
     * @param title 생성할 할 일 제목 (빈 값 불가)
     * @return 생성된 할 일 DTO
     */
    ToDoDto createTodo(String title);

    /**
     * 지정된 ID의 할 일을 삭제합니다.
     *
     * @param id 삭제할 할 일의 ID (양수여야 함)
     */
    void deleteTodo(Long id);

    /**
     * 지정된 ID의 할 일 제목을 수정합니다.
     *
     * @param id    수정할 할 일의 ID (양수여야 함)
     * @param title 새로운 할 일 제목
     * @return 수정된 할 일 DTO
     */
    ToDoDto updateTodoTitle(Long id, String title);

    /**
     * 지정된 ID의 할 일 완료 상태를 변경합니다.
     *
     * @param id        수정할 할 일의 ID (양수여야 함)
     * @param completed 새로운 완료 상태 (true 또는 false)
     * @return 수정된 할 일 DTO
     */
    ToDoDto updateTodoCompleted(Long id, Boolean completed);
}
