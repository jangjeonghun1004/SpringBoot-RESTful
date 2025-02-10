package com.example.demo.controller;

import com.example.demo.dto.ApiResult;
import com.example.demo.dto.todo.SaveToDoRequest;
import com.example.demo.dto.todo.ToDoDto;
import com.example.demo.dto.todo.UpdateToDoCompletedRequest;
import com.example.demo.dto.todo.UpdateToDoTitleRequest;
import com.example.demo.provider.MessageProvider;
import com.example.demo.service.ToDoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 할 일(ToDo) 관리 컨트롤러
 * - 할 일 조회, 생성, 수정, 삭제 기능 제공
 * - 모든 응답은 ApiResult 형식으로 래핑하여 클라이언트에 반환
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class ToDoController {

    private final ToDoService toDoService;

    /**
     * 할 일 목록 조회
     * @return 할 일 리스트 응답
     */
    @GetMapping("/fetch")
    public ResponseEntity<ApiResult<List<ToDoDto>>> fetchToDo() {
        return ResponseEntity.ok(new ApiResult<>(
                true,
                MessageProvider.getMessage("common.success"),
                this.toDoService.fetchToDo()
        ));
    }

    /**
     * 새로운 할 일 저장
     * @param saveToDoRequest 저장할 할 일 데이터
     * @return 저장된 할 일 정보
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResult<ToDoDto>> saveToDo(@RequestBody @Valid SaveToDoRequest saveToDoRequest) {
        return ResponseEntity.ok(new ApiResult<>(
                true,
                MessageProvider.getMessage("common.success"),
                this.toDoService.saveToDo(saveToDoRequest)
        ));
    }

    /**
     * 할 일 제목 수정
     * @param updateToDoTitleRequest 제목 변경 요청 데이터
     * @return 변경된 할 일 정보
     */
    @PatchMapping("/updateTitle")
    public ResponseEntity<ApiResult<ToDoDto>> updateToDoTitle(@RequestBody @Valid UpdateToDoTitleRequest updateToDoTitleRequest) {
        return ResponseEntity.ok(new ApiResult<>(
                true,
                MessageProvider.getMessage("common.success"),
                this.toDoService.updateToDoTitle(updateToDoTitleRequest)
        ));
    }

    /**
     * 할 일 완료 상태 변경
     * @param updateToDoCompletedRequest 완료 상태 변경 요청 데이터
     * @return 변경된 할 일 정보
     */
    @PatchMapping("/updateCompleted")
    public ResponseEntity<ApiResult<ToDoDto>> updateToDoCompleted(@RequestBody @Valid UpdateToDoCompletedRequest updateToDoCompletedRequest) {
        return ResponseEntity.ok(new ApiResult<>(
                true,
                MessageProvider.getMessage("common.success"),
                this.toDoService.updateToDoCompleted(updateToDoCompletedRequest)
        ));
    }

    /**
     * 할 일 삭제
     * @param id 삭제할 할 일 ID
     * @return 성공 메시지 응답
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResult<Object>> deleteToDo(@PathVariable Long id) {
        this.toDoService.deleteToDo(id);
        return ResponseEntity.ok(new ApiResult<>(
                true,
                MessageProvider.getMessage("common.success"),
                new Object() // null 반환 방지
        ));
    }
}
