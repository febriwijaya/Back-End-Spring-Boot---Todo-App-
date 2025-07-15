package com.myproject.todo_management.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor

public class TodoAPIException extends RuntimeException {
    private HttpStatus status;
    private String message;

}
