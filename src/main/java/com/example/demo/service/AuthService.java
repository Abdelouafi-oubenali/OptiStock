package com.example.demo.service;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.entity.User;

public interface AuthService {
    User register(RegisterDTO registerDTO);
    User login(LoginDTO loginDTO);
    boolean emailExists(String email);
    User getCurrentUser();
}