package com.example.demo.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {

    @Test
    void userNotFoundException_ShouldCreateWithMessage() {
        String errorMessage = "User not found with id: 123";

        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void userNotFoundException_WithEmptyMessage_ShouldCreateSuccessfully() {
        String emptyMessage = "";

        UserNotFoundException exception = new UserNotFoundException(emptyMessage);

        assertNotNull(exception);
        assertEquals(emptyMessage, exception.getMessage());
    }

    @Test
    void userNotFoundException_WithNullMessage_ShouldCreateSuccessfully() {
        UserNotFoundException exception = new UserNotFoundException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void userNotFoundException_ShouldHaveCorrectSuperClass() {
        String errorMessage = "User not found";


        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        // Assert
        assertTrue(exception instanceof RuntimeException,
                "UserNotFoundException should extend RuntimeException");
    }

    @Test
    void userNotFoundException_ShouldPreserveMessageExactly() {

        String detailedMessage = "User with email 'test@example.com' not found in database";

        UserNotFoundException exception = new UserNotFoundException(detailedMessage);

        assertEquals(detailedMessage, exception.getMessage());
    }

    @Test
    void userNotFoundException_ShouldBeThrowable() {
        String errorMessage = "User not found";

        assertThrows(UserNotFoundException.class, () -> {
            throw new UserNotFoundException(errorMessage);
        });
    }

    @Test
    void userNotFoundException_StackTraceShouldBeAvailable() {
        // Arrange
        String errorMessage = "User not found";

        // Act
        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        // Assert
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    void userNotFoundException_ShouldWorkInTryCatchBlock() {
        // Arrange
        String errorMessage = "User not found with ID: 456";
        boolean exceptionCaught = false;

        try {
            throw new UserNotFoundException(errorMessage);
        } catch (UserNotFoundException e) {
            exceptionCaught = true;
            assertEquals(errorMessage, e.getMessage());
        }

        assertTrue(exceptionCaught, "UserNotFoundException should be caught in catch block");
    }
}