package com.myproject.todo_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRegisterDto {
    private Long id;
    @NotBlank(message = "name cannot be empty")
    @Pattern(
            regexp = "^[\\p{L} ]+$",
            message = "name must only contain letters and spaces"
    )
    private String name;

    @NotBlank(message = "username cannot be empty")
    private String username;

    @NotBlank(message = "email cannot be empty")
    private String email;

    @NotNull(message = "birth date cannot be null")
    private LocalDate birthDate;

    @NotBlank(message = "job title cannot be empty")
    private String jobTitle;

    @NotBlank(message = "location cannot be empty")
    private String location;

    private String profilePhoto;
}
