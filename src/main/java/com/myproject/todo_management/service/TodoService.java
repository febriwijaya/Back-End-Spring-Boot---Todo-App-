package com.myproject.todo_management.service;

import com.myproject.todo_management.dto.PagedResponse;
import com.myproject.todo_management.dto.TodoDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TodoService {

    TodoDto addTodo(TodoDto todoDto);

    TodoDto getTodo(Long id);

    PagedResponse<TodoDto> getAllTodos(Pageable pageable);

    TodoDto updateTodo(TodoDto todoDto, Long id);

    void deleteTodo(Long id);

    TodoDto completedTodo(Long id);

    TodoDto inCompleteTodo(Long id);

}
