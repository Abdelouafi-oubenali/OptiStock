package com.example.demo.service;

import com.example.demo.entity.User;

public interface AuthService {
    boolean login(String email , String pass) ;
    User register(User user) ;
}
