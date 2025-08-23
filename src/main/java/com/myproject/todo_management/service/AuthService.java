package com.myproject.todo_management.service;

import com.myproject.todo_management.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AuthService {
    String register(RegisterDto registerDto, MultipartFile photo) throws IOException;

    String updateRegister(Long userId, UpdateRegisterDto updateRegisterDto, MultipartFile photo) throws IOException;

    String deleteRegister(Long userId) throws IOException;

    JwtAuthResponse login(LoginDto loginDto);

    PagedResponse<RegisterDto> getAllRegister(Pageable pageable);

    RegisterDto  getUserById(Long id);

    RegisterDto getUserByUsernameOrEmail(String usernameOrEmail);

    String updatePassword(UpdatePasswordDto dto, String username);

}
