package com.example.demo.service;

import com.example.demo.dto.todo.SaveToDoRequest;
import com.example.demo.dto.todo.ToDoDto;
import com.example.demo.dto.todo.UpdateToDoTitleRequest;
import com.example.demo.dto.todo.UpdateToDoCompletedRequest;

import java.util.List;

public interface ToDoService {
    List<ToDoDto> fetchToDo();
    ToDoDto saveToDo(SaveToDoRequest saveToDoRequest);
    void deleteToDo(Long id);
    ToDoDto updateToDoTitle(UpdateToDoTitleRequest updateToDoTitleRequest);
    ToDoDto updateToDoCompleted(UpdateToDoCompletedRequest updateToDoCompletedRequest);
}
