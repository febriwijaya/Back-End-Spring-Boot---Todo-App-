package com.myproject.todo_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    @NotBlank(message = "name cannot be empty")
    @Pattern(
            regexp = "^[\\p{L} ]+$",
            message = "name must only contain letters and spaces"
    )
    private String name;

    @NotBlank(message = "username cannot be empty")
    private String username;

    @NotBlank(message = "email cannot be empty")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "password cannot be empty")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters and contain letters, numbers, and special characters"
    )
    private String password;
}
