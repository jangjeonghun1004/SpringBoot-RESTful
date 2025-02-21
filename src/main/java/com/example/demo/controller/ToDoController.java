package com.example.demo.controller;

import com.example.demo.dto.ApiResult;
import com.example.demo.dto.todo.ToDoCreateRequest;
import com.example.demo.dto.todo.ToDoDto;
import com.example.demo.dto.todo.UpdateToDoCompletedRequest;
import com.example.demo.dto.todo.UpdateToDoTitleRequest;
import com.example.demo.service.ToDoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 할 일(ToDo) REST API 컨트롤러
 *
 * 할 일 조회, 생성, 수정, 삭제 기능을 제공하며
 * 모든 응답은 ApiResult 형식으로 래핑하여 클라이언트에 반환됩니다.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class ToDoController {

    private final ToDoService toDoService;

    /**
     * 할 일 목록 조회 API
     * URL: GET /api/todo
     *
     * @return 할 일 리스트 응답
     */
    @GetMapping
    public ResponseEntity<ApiResult<List<ToDoDto>>> fetch() {
        List<ToDoDto> toDoList = toDoService.findAllTodos();
        return ResponseEntity.ok(ApiResult.success(toDoList));
    }

    /**
     * 새로운 할 일 저장 API
     * URL: POST /api/todo
     *
     * @param request 할 일 생성 요청 데이터 (제목)
     * @return 저장된 할 일 정보
     */
    @PostMapping
    public ResponseEntity<ApiResult<ToDoDto>> save(@RequestBody @Valid ToDoCreateRequest request) {
        ToDoDto savedToDo = toDoService.createTodo(request.getTitle());
        return ResponseEntity.ok(ApiResult.success(savedToDo));
    }

    /**
     * 할 일 제목 수정 API
     * URL: PATCH /api/todo/updateTitle
     *
     * @param request 할 일 제목 수정 요청 데이터 (id, title)
     * @return 변경된 할 일 정보
     */
    @PatchMapping("/updateTitle")
    public ResponseEntity<ApiResult<ToDoDto>> updateTitle(@RequestBody @Valid UpdateToDoTitleRequest request) {
        ToDoDto updatedToDo = toDoService.updateTodoTitle(request.getId(), request.getTitle());
        return ResponseEntity.ok(ApiResult.success(updatedToDo));
    }

    /**
     * 할 일 완료 상태 변경 API
     * URL: PATCH /api/todo/updateCompleted
     *
     * @param request 할 일 완료 상태 변경 요청 데이터 (id, completed)
     * @return 변경된 할 일 정보
     */
    @PatchMapping("/updateCompleted")
    public ResponseEntity<ApiResult<ToDoDto>> updateCompleted(@RequestBody @Valid UpdateToDoCompletedRequest request) {
        ToDoDto updatedToDo = toDoService.updateTodoCompleted(request.getId(), request.getCompleted());
        return ResponseEntity.ok(ApiResult.success(updatedToDo));
    }

    /**
     * 할 일 삭제 API
     * URL: DELETE /api/todo/delete/{id}
     *
     * @param id 삭제할 할 일 ID (양수여야 함)
     * @return 삭제된 할 일 ID를 포함한 응답
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResult<Long>> delete(@PathVariable @Positive(message = "{common.validation.positive}") Long id) {
        toDoService.deleteTodo(id);
        return ResponseEntity.ok(ApiResult.success(id));
    }
}
