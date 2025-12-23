package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder ;

    public UserServiceImpl(UserRepository userRepository , PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder ;
    }

    @Override
    public ApiResponse saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return new ApiResponse(false, "Un utilisateur avec cet email existe déjà");
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return new ApiResponse(true, "Utilisateur ajouté avec succès");
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec id: " + id));
    }

    @Override
    public ApiResponse updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    user.setEmail(updatedUser.getEmail());
                    user.setPassword(updatedUser.getPassword());
                    user.setRole(updatedUser.getRole());
                    userRepository.save(user);
                    return new ApiResponse(true, "Utilisateur mis à jour avec succès");
                })
                .orElse(new ApiResponse(false, "Utilisateur non trouvé"));
    }

    @Override
    public ApiResponse deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return new ApiResponse(true, "Utilisateur supprimé avec succès");
        }
        return new ApiResponse(false, "Utilisateur non trouvé");
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}