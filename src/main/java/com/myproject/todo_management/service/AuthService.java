package com.myproject.todo_management.service;

import com.myproject.todo_management.dto.JwtAuthResponse;
import com.myproject.todo_management.dto.LoginDto;
import com.myproject.todo_management.dto.RegisterDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AuthService {
    String register(RegisterDto registerDto, MultipartFile photo) throws IOException;

    String updateRegister(Long userId, RegisterDto registerDto, MultipartFile photo) throws IOException;

    String deleteRegister(Long userId) throws IOException;

    JwtAuthResponse login(LoginDto loginDto);

    List<RegisterDto> getAllRegister();

    RegisterDto  getUserById(Long id);

    RegisterDto getUserByUsernameOrEmail(String usernameOrEmail);

}
