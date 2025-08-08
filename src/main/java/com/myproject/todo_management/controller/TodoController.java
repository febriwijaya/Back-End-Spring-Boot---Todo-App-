package com.myproject.todo_management.controller;

import com.myproject.todo_management.dto.TodoDto;
import com.myproject.todo_management.service.TodoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("api/todos")
@AllArgsConstructor
@Slf4j //-- tambah logger untuk tangkap error
public class TodoController {

    private TodoService todoService;

    // Build Add Todo REST API
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TodoDto> addTodo(@Valid @RequestBody TodoDto todoDto) {
        try {
            TodoDto savedTodo = todoService.addTodo(todoDto);
            return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace
            throw e;
        }
    }

    // Build Get TODO REST API
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("{id}")
    public ResponseEntity<TodoDto> getTodo(@PathVariable("id") Long todoId) {
        try {
            TodoDto todoDto = todoService.getTodo(todoId);
            return new ResponseEntity<>(todoDto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace
            throw e;
        }
    }

    // Build Get All Todos REST API
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<TodoDto>> getAllTodos() {
        try {
            List<TodoDto> todos = todoService.getAllTodos();

            return ResponseEntity.ok(todos);
        }  catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace
            throw e;
        }

    }

    // Build Update TODO REST API
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<TodoDto> updateTodo(@Valid @RequestBody TodoDto todoDto, @PathVariable("id") Long todoId) {
        try {
            TodoDto updateTodo =  todoService.updateTodo(todoDto, todoId);
            return ResponseEntity.ok(updateTodo);
        } catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace
            throw e;
        }
    }

    // Build delete TODO rest api
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
     public ResponseEntity<String> deleteTodo(@PathVariable("id") Long todoId) {
        try {
            todoService.deleteTodo(todoId);
            return ResponseEntity.ok("Todo deleted Successfully!");
        } catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace
            throw e;
        }
     }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
     @PatchMapping("{id}/complete")
     public ResponseEntity<TodoDto> completedTodo(@PathVariable("id") Long todoId) {
        try {
            TodoDto updatedTodo = todoService.completedTodo(todoId);
            return ResponseEntity.ok(updatedTodo);
        } catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace
            throw e;
        }
     }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("{id}/in-complete")
    public ResponseEntity<TodoDto> InCompletedTodo(@PathVariable("id") Long todoId) {
        try {
            TodoDto updatedTodo = todoService.inCompleteTodo(todoId);
            return ResponseEntity.ok(updatedTodo);
        } catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace
            throw e;
        }
    }
}
