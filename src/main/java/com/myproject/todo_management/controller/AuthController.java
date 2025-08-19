package com.myproject.todo_management.controller;

import com.myproject.todo_management.dto.*;
import com.myproject.todo_management.exception.ErrorDetails;
import com.myproject.todo_management.exception.TodoAPIException;
import com.myproject.todo_management.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j //-- tambah logger untuk tangkap error
public class AuthController {

    private AuthService authService;

    // Build Register REST API

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
       try {
           List <RegisterDto> listUser = authService.getAllRegister();
           return ResponseEntity.ok(listUser);
       } catch (Exception e) {
           log.error("There is an error", e); // log error + stack trace

           ErrorDetails errorDetails = new ErrorDetails(
                   LocalDateTime.now(),
                   "Failed to fetch users",
                   e.getMessage()
           );

           return ResponseEntity
                   .status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(errorDetails);
       }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            RegisterDto registerDto = authService.getUserById(id);
            return new ResponseEntity<>(registerDto, HttpStatus.OK);
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Failed to fetch user with id : " + id
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace

            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occurred, Failed to fetch user with id: " + id,
                    e.getMessage()
            );

            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/users/username/{usernameOrEmail}")
    public ResponseEntity<?> getUserByUsernameOrEmail(@PathVariable String usernameOrEmail) {
        try {
            RegisterDto registerDto =  authService.getUserByUsernameOrEmail(usernameOrEmail);
            return new ResponseEntity<>(registerDto, HttpStatus.OK);
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Failed to fetch user with username or email : " + usernameOrEmail
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace

            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occurred, Failed to fetch user with username or email : " + usernameOrEmail,
                    e.getMessage()
            );

            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(
            value = "/register",
            consumes = { "multipart/form-data" }
    )
    public ResponseEntity<?> register(
            @RequestPart("data") @Valid RegisterDto registerDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try {
            String response = authService.register(registerDto, photo);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Custom business error"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (IOException ioEx) {
            log.error("Error while processing photo upload", ioEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Photo upload failed",
                    ioEx.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
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
    @PutMapping(
            value = "/update/{id}",
            consumes = { "multipart/form-data" }
    )
    public ResponseEntity<?> updateRegister(
            @PathVariable Long id,
            @RequestPart("data") @Valid UpdateRegisterDto updateRegisterDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try {
            String response = authService.updateRegister(id, updateRegisterDto, photo);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Custom business error"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (IOException ioEx) {
            log.error("Error while processing photo upload", ioEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Photo upload failed",
                    ioEx.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRegister(@PathVariable Long id) {
        try {
            String response = authService.deleteRegister(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(TodoAPIException apiEx) {
            log.error("Business error while deleteing user", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Custom business error"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (IOException ioEx) {
            log.error("Error while deleting photo", ioEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Deleted failed",
                    ioEx.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
        } catch (Exception e) {
            log.error("Unexpected error while deleting user", e);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occured",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            JwtAuthResponse jwtAuthResponse = authService.login(loginDto);
            return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
        } catch(TodoAPIException apiEx) {
            log.error("Login failed : ", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Login failed"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        }
        catch (Exception e) {
            log.error("Unexpected error during login", e);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error during login",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(
            @RequestBody @Valid UpdatePasswordDto passwordDto) {
        try {
            String response = authService.updatePassword(passwordDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TodoAPIException apiEx) {
            log.error("There is an error", apiEx);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    apiEx.getMessage(),
                    "Password update failed"
            );
            return ResponseEntity.status(apiEx.getStatus()).body(errorDetails);
        } catch (Exception e) {
            log.error("Unexpected error during password update", e);
            ErrorDetails errorDetails = new ErrorDetails(
                    LocalDateTime.now(),
                    "Unexpected error occurred",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
    }
}
