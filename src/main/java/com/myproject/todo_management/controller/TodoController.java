package com.myproject.todo_management.controller;

import com.myproject.todo_management.dto.TodoDto;
import com.myproject.todo_management.exception.ErrorDetails;
import com.myproject.todo_management.exception.TodoAPIException;
import com.myproject.todo_management.service.TodoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("api/todos")
@AllArgsConstructor
@Slf4j //-- tambah logger untuk tangkap error
public class TodoController {

    private TodoService todoService;

    // Build Add Todo REST API
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<?> addTodo(@Valid @RequestBody TodoDto todoDto) {
        try {
            TodoDto savedTodo = todoService.addTodo(todoDto);
            return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Custom business error"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (Exception e) {
            log.error("There is an error", e);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occurred",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
    }

    // Build Get TODO REST API
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("{id}")
    public ResponseEntity<?> getTodo(@PathVariable("id") Long todoId) {
        try {
            TodoDto todoDto = todoService.getTodo(todoId);
            return new ResponseEntity<>(todoDto, HttpStatus.OK);
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Custom business error"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (Exception e) {
            log.error("There is an error", e);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occurred",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
    }

    // Build Get All Todos REST API
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<?> getAllTodos() {
        try {
            List<TodoDto> todos = todoService.getAllTodos();

            return ResponseEntity.ok(todos);
        }  catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Custom business error"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (Exception e) {
            log.error("There is an error", e);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occurred",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }

    }

    // Build Update TODO REST API
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("{id}")
    public ResponseEntity<?> updateTodo(@Valid @RequestBody TodoDto todoDto, @PathVariable("id") Long todoId) {
        try {
            TodoDto updateTodo =  todoService.updateTodo(todoDto, todoId);
            return ResponseEntity.ok(updateTodo);
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Custom business error"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (Exception e) {
            log.error("There is an error", e);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occurred",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
    }

    // Build delete TODO rest api
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("{id}")
     public ResponseEntity<?> deleteTodo(@PathVariable("id") Long todoId) {
        try {
            todoService.deleteTodo(todoId);
            return ResponseEntity.ok("Todo deleted Successfully!");
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Custom business error"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (Exception e) {
            log.error("There is an error", e);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occurred",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
     }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
     @PatchMapping("{id}/complete")
     public ResponseEntity<?> completedTodo(@PathVariable("id") Long todoId) {
        try {
            TodoDto updatedTodo = todoService.completedTodo(todoId);
            return ResponseEntity.ok(updatedTodo);
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Custom business error"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (Exception e) {
            log.error("There is an error", e);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occurred",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
     }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("{id}/in-complete")
    public ResponseEntity<?> InCompletedTodo(@PathVariable("id") Long todoId) {
        try {
            TodoDto updatedTodo = todoService.inCompleteTodo(todoId);
            return ResponseEntity.ok(updatedTodo);
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Custom business error"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (Exception e) {
            log.error("There is an error", e);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occurred",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
    }
}
