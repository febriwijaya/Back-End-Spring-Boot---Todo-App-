package com.myproject.todo_management.service;

import com.myproject.todo_management.dto.JwtAuthResponse;
import com.myproject.todo_management.dto.LoginDto;
import com.myproject.todo_management.dto.RegisterDto;

public interface AuthService {
    String register(RegisterDto registerDto);

    JwtAuthResponse login(LoginDto loginDto);
}
