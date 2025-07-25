package com.myproject.todo_management.controller;

import com.myproject.todo_management.dto.TodoDto;
import com.myproject.todo_management.service.TodoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("api/todos")
@AllArgsConstructor
public class TodoController {

    private TodoService todoService;

    // Build Add Todo REST API
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TodoDto> addTodo(@RequestBody TodoDto todoDto) {

        TodoDto savedTodo = todoService.addTodo(todoDto);

        return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
    }

    // Build Get TODO REST API
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("{id}")
    public ResponseEntity<TodoDto> getTodo(@PathVariable("id") Long todoId) {
        TodoDto todoDto = todoService.getTodo(todoId);
        return new ResponseEntity<>(todoDto, HttpStatus.OK);
    }

    // Build Get All Todos REST API
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<TodoDto>> getAllTodos() {
        List<TodoDto> todos = todoService.getAllTodos();

        return ResponseEntity.ok(todos);
    }

    // Build Update TODO REST API
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<TodoDto> updateTodo(@RequestBody TodoDto todoDto, @PathVariable("id") Long todoId) {
        TodoDto updateTodo =  todoService.updateTodo(todoDto, todoId);
        return ResponseEntity.ok(updateTodo);
    }

    // Build delete TODO rest api
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
     public ResponseEntity<String> deleteTodo(@PathVariable("id") Long todoId) {
        todoService.deleteTodo(todoId);
        return ResponseEntity.ok("Todo deleted Successfully!");
     }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
     @PatchMapping("{id}/complete")
     public ResponseEntity<TodoDto> completedTodo(@PathVariable("id") Long todoId) {
        TodoDto updatedTodo = todoService.completedTodo(todoId);
        return ResponseEntity.ok(updatedTodo);
     }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("{id}/in-complete")
    public ResponseEntity<TodoDto> InCompletedTodo(@PathVariable("id") Long todoId) {
        TodoDto updatedTodo = todoService.inCompleteTodo(todoId);
        return ResponseEntity.ok(updatedTodo);
    }
}
