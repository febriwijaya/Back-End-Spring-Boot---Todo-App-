package com.myproject.todo_management.dto;

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
public class UpdatePasswordDto {
    @NotBlank(message = "old password cannot be empty")
    private String oldPassword;

    @NotBlank(message = "new password cannot be empty")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "New Password must be at least 8 characters and contain letters, numbers, and special characters"
    )
    private String newPassword;

    @NotBlank(message = "confirm password cannot be empty")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Confirm Password must be at least 8 characters and contain letters, numbers, and special characters"
    )
    private String confirmPassword;
}
