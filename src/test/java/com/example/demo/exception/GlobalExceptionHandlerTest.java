package com.example.demo.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleValidationExceptions_ShouldReturnValidationErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("objectName", "field1", "Field 1 is required");
        FieldError fieldError2 = new FieldError("objectName", "field2", "Field 2 must be valid");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, String> errors = response.getBody();
        assertEquals(2, errors.size());
        assertEquals("Field 1 is required", errors.get("field1"));
        assertEquals("Field 2 must be valid", errors.get("field2"));
    }

    @Test
    void handleValidationExceptions_WithEmptyErrors_ShouldReturnEmptyMap() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList());

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void handleRuntimeException_ShouldReturnErrorMessage() {
        String errorMessage = "Une erreur s'est produite";
        RuntimeException ex = new RuntimeException(errorMessage);

        ResponseEntity<String> response = globalExceptionHandler.handleRuntimeException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleRuntimeException_WithNullMessage_ShouldReturnNull() {
        RuntimeException ex = new RuntimeException();

        ResponseEntity<String> response = globalExceptionHandler.handleRuntimeException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleRuntimeException_WithEmptyMessage_ShouldReturnEmptyString() {
        RuntimeException ex = new RuntimeException("");

        ResponseEntity<String> response = globalExceptionHandler.handleRuntimeException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void handleValidationExceptions_WithMultipleFieldErrors_ShouldReturnAllErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("user", "email", "Email must be valid");
        FieldError fieldError2 = new FieldError("user", "password", "Password must be at least 8 characters");
        FieldError fieldError3 = new FieldError("user", "name", "Name is required");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2, fieldError3));

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, String> errors = response.getBody();
        assertEquals(3, errors.size());
        assertEquals("Email must be valid", errors.get("email"));
        assertEquals("Password must be at least 8 characters", errors.get("password"));
        assertEquals("Name is required", errors.get("name"));
    }

    @Test
    void handleRuntimeException_WithCustomRuntimeException_ShouldHandleCorrectly() {
        String customMessage = "Resource not found";
        RuntimeException ex = new RuntimeException(customMessage);

        ResponseEntity<String> response = globalExceptionHandler.handleRuntimeException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(customMessage, response.getBody());
    }
}