package com.example.demo.service;

import com.example.demo.dto.todo.SaveToDoRequest;
import com.example.demo.dto.todo.ToDoDto;
import com.example.demo.dto.todo.UpdateToDoTitleRequest;
import com.example.demo.dto.todo.UpdateToDoCompletedRequest;
import com.example.demo.entity.ToDo;
import com.example.demo.repository.ToDoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToDoServiceImpl implements ToDoService {

    // ToDo 엔티티에 대한 데이터 접근 인터페이스
    private final ToDoRepository toDoRepository;

    /**
     * 모든 할 일(ToDo) 엔티티를 조회하여 DTO 목록으로 반환합니다.
     * 읽기 전용 트랜잭션을 적용하여 성능을 최적화합니다.
     *
     * @return 할 일 DTO 목록
     */
    @Override
    @Transactional(readOnly = true) // 데이터 일관성을 유지하면서, 성능을 최적화하기 위해 읽기 전용 트랜잭션 적용
    public List<ToDoDto> fetchToDo() {
        return toDoRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 할 일(ToDo) 항목을 저장합니다.
     * 트랜잭션을 적용하여 데이터 일관성을 유지합니다.
     *
     * @param saveToDoRequest 저장할 할 일의 정보를 담은 DTO
     * @return 저장된 할 일의 정보를 담은 DTO
     */
    @Override
    @Transactional // 트랜잭션 적용: 저장 과정에서의 데이터 일관성을 보장합니다.
    public ToDoDto saveToDo(SaveToDoRequest saveToDoRequest) {
        ToDo toDo = ToDo.builder()
                .title(saveToDoRequest.getTitle())
                .completed(false)
                .build();

        ToDo savedToDo = toDoRepository.save(toDo);
        return convertToDto(savedToDo);
    }

    /**
     * 할 일 제목을 업데이트합니다.
     * 트랜잭션을 적용하여 변경된 데이터를 안전하게 반영할 수 있도록 합니다.
     *
     * @param updateRequest 업데이트할 할 일의 ID와 새 제목 정보를 담은 요청 객체
     * @return 업데이트된 할 일의 정보를 담은 DTO
     * @throws IllegalArgumentException 주어진 ID에 해당하는 할 일이 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional // 트랜잭션 적용: 변경 감지를 이용하여 안전하게 제목을 업데이트합니다.
    public ToDoDto updateToDoTitle(UpdateToDoTitleRequest updateRequest) {
        ToDo toDo = toDoRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 id를 가진 할 일이 존재하지 않습니다. id: " + updateRequest.getId()
                ));

        toDo.updateTitle(updateRequest.getTitle());
        return convertToDto(toDo);
    }

    /**
     * 할 일 완료 상태를 업데이트합니다.
     * 트랜잭션을 적용하여 변경된 데이터를 안전하게 반영할 수 있도록 합니다.
     *
     * @param updateToDoCompletedRequest 업데이트할 할 일의 ID와 새 완료 상태 정보를 담은 요청 객체
     * @return 업데이트된 할 일의 정보를 담은 DTO
     * @throws IllegalArgumentException 주어진 ID에 해당하는 할 일이 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional // 트랜잭션 적용: 변경 감지를 이용하여 안전하게 완료 상태를 업데이트합니다.
    public ToDoDto updateToDoCompleted(UpdateToDoCompletedRequest updateToDoCompletedRequest) {
        ToDo toDo = toDoRepository.findById(updateToDoCompletedRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 id를 가진 할 일이 존재하지 않습니다. id: " + updateToDoCompletedRequest.getId()));

        toDo.updateCompleted(updateToDoCompletedRequest.getCompleted());
        return convertToDto(toDo);
    }

    /**
     * 할 일(ToDo) 항목을 삭제합니다.
     * 트랜잭션을 적용하여 삭제 과정에서의 데이터 일관성을 유지합니다.
     *
     * @param id 삭제할 할 일의 ID
     * @throws IllegalArgumentException 주어진 ID에 해당하는 할 일이 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional // 트랜잭션 적용: 삭제 과정에서의 데이터 일관성을 유지합니다.
    public void deleteToDo(Long id) {
        // 삭제 전에 해당 ID의 엔티티가 존재하는지 확인합니다.
        ToDo toDo = toDoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id를 가진 할 일이 존재하지 않습니다. id: " + id));

        toDoRepository.delete(toDo);
    }

    /**
     * ToDo 엔티티를 ToDoDto로 변환합니다.
     *
     * @param toDo 변환할 ToDo 엔티티
     * @return 변환된 ToDoDto 객체
     */
    private ToDoDto convertToDto(ToDo toDo) {
        return ToDoDto.builder()
                .id(toDo.getId())
                .title(toDo.getTitle())
                .completed(toDo.getCompleted())
                .build();
    }
}
