package com.myproject.todo_management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {

    private Long id;
    @NotBlank(message = "title cannot be empty")
    private String title;

    @NotBlank(message = "title cannot be empty")
    private String description;
    private boolean completed;
    private String createdBy;
    private String updatedBy;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy:MM:dd HH:mm:ss")
//    private LocalDateTime timeCreated;
//
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy:MM:dd HH:mm:ss")
//    private LocalDateTime timeUpdated;
}
