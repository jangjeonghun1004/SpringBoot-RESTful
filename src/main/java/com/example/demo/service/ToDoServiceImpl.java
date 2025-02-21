package com.example.demo.service;

import com.example.demo.dto.todo.ToDoDto;
import com.example.demo.entity.Todo;
import com.example.demo.provider.MessageProvider;
import com.example.demo.repository.ToDoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 할 일(ToDo) 서비스 구현 클래스
 *
 * 할 일 목록 조회, 생성, 수정, 삭제 기능을 제공하며,
 */
@Service
@RequiredArgsConstructor
public class ToDoServiceImpl implements ToDoService {

    /**
     * 할 일 엔티티에 대한 데이터 접근 객체
     */
    private final ToDoRepository toDoRepository;

    /**
     * 모든 할 일 목록을 조회하여 DTO 리스트로 반환합니다.
     *
     * @return 할 일 DTO 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<ToDoDto> findAllTodos() {
        return toDoRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 할 일 항목을 생성하고 저장합니다.
     *
     * @param title 저장할 할 일의 제목 (빈 값 불가)
     * @return 생성된 할 일 DTO
     */
    @Override
    @Transactional
    public ToDoDto createTodo(String title) {
        Todo todo = Todo.builder()
                .title(title)
                .completed(false)
                .build();

        Todo savedTodo = toDoRepository.save(todo);
        return convertToDto(savedTodo);
    }

    /**
     * 주어진 ID에 해당하는 할 일의 제목을 업데이트합니다.
     *
     * @param id    업데이트할 할 일의 ID (양수여야 함)
     * @param title 새로운 할 일 제목
     * @return 업데이트된 할 일 DTO
     * @throws IllegalArgumentException 주어진 ID에 해당하는 할 일이 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional
    public ToDoDto updateTodoTitle(Long id, String title) {
        Todo todo = toDoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        MessageProvider.getMessage("common.validation.illegalArgumentException") + " id: " + id));
        todo.updateTitle(title);
        return convertToDto(todo);
    }

    /**
     * 주어진 ID에 해당하는 할 일의 완료 상태를 업데이트합니다.
     *
     * @param id        업데이트할 할 일의 ID (양수여야 함)
     * @param completed 새로운 완료 상태 (true 또는 false)
     * @return 업데이트된 할 일 DTO
     * @throws IllegalArgumentException 주어진 ID에 해당하는 할 일이 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional
    public ToDoDto updateTodoCompleted(Long id, Boolean completed) {
        Todo todo = toDoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        MessageProvider.getMessage("common.validation.illegalArgumentException") + " id: " + id));
        todo.updateCompleted(completed);
        return convertToDto(todo);
    }

    /**
     * 주어진 ID에 해당하는 할 일 항목을 삭제합니다.
     *
     * @param id 삭제할 할 일의 ID (양수여야 함)
     * @throws IllegalArgumentException 주어진 ID에 해당하는 할 일이 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional
    public void deleteTodo(Long id) {
        Todo todo = toDoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        MessageProvider.getMessage("common.validation.illegalArgumentException") + " id: " + id));
        toDoRepository.delete(todo);
    }

    /**
     * 할 일 엔티티를 할 일 DTO로 변환합니다.
     *
     * @param todo 변환할 할 일 엔티티
     * @return 변환된 할 일 DTO
     */
    private ToDoDto convertToDto(Todo todo) {
        return ToDoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .completed(todo.getCompleted())
                .build();
    }
}
