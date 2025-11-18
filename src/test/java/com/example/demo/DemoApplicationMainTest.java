package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import static org.junit.jupiter.api.Assertions.*;

class DemoApplicationMainTest {

    @Test
    void main_WithEmptyArgs_ShouldNotThrowException() {
        String[] args = {};

        assertDoesNotThrow(() -> DemoApplication.main(args));
    }

    @Test
    void main_WithNullArgs_ShouldNotThrowException() {
        String[] args = null;

        assertDoesNotThrow(() -> DemoApplication.main(args));
    }

    @Test
    void main_WithSpringProfileArgs_ShouldNotThrowException() {
        String[] args = {"--spring.profiles.active=test"};

        assertDoesNotThrow(() -> DemoApplication.main(args));
    }

    @Test
    void applicationClass_ShouldBeAnnotatedWithSpringBootApplication() {
        Class<DemoApplication> clazz = DemoApplication.class;

        assertTrue(clazz.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    void applicationClass_ShouldHaveMainMethod() {
        Class<DemoApplication> clazz = DemoApplication.class;

        assertDoesNotThrow(() -> {
            clazz.getMethod("main", String[].class);
        }, "La classe devrait avoir une méthode main");
    }
}