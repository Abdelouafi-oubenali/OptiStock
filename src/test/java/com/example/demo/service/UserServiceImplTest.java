package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("abdelouafi@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.CLIENT);
    }

    @Test void TestSaveUser()
    {
        when(userRepository.existsByEmail("abdelouafi@gmail.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user) ;

        ApiResponse response = userService.saveUser(user);
        assertTrue(response.isSuccess());
        assertEquals("Utilisateur ajouté avec succès", response.getMessage());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSaveUser_EmailExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        ApiResponse response = userService.saveUser(user);

        assertFalse(response.isSuccess());
        assertEquals("Un utilisateur avec cet email existe déjà", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User found = userService.getUserById(user.getId());

        assertNotNull(found);
        assertEquals("John", found.getFirstName());
    }

    @Test
    void testGetUserById_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(randomId));
    }

    @Test
    void testUpdateUser_Success() {
        User updated = new User();
        updated.setFirstName("Jane");
        updated.setLastName("Smith");
        updated.setEmail("jane.smith@example.com");
        updated.setPassword("newpass");
        updated.setRole(Role.ADMIN);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        ApiResponse response = userService.updateUser(user.getId(), updated);

        assertTrue(response.isSuccess());
        assertEquals("Utilisateur mis à jour avec succès", response.getMessage());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(user.getId());

        ApiResponse response = userService.deleteUser(user.getId());

        assertTrue(response.isSuccess());
        assertEquals("Utilisateur supprimé avec succès", response.getMessage());
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void testDeleteUser_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.existsById(randomId)).thenReturn(false);

        ApiResponse response = userService.deleteUser(randomId);

        assertFalse(response.isSuccess());
        assertEquals("Utilisateur non trouvé", response.getMessage());
        verify(userRepository, never()).deleteById(any());
    }


    @Test
    void testGetAllUsers() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }



}
