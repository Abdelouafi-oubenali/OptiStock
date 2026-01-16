package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    ApiResponse saveUser(User user);
    UserDTO getUserById(UUID id);
    ApiResponse updateUser(UUID id, User user);
    ApiResponse deleteUser(UUID id);
    List<UserDTO> getAllUsers();
    UserDTO getUserByEmail(String email) ;
}
