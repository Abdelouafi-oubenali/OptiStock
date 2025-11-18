package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DemoApplicationMainTest {

    @Test
    void main_WithEmptyArgs_ShouldCompile() {
        assertDoesNotThrow(() -> {
            Class<DemoApplication> clazz = DemoApplication.class;
            clazz.getMethod("main", String[].class);
        });
    }

    @Test
    void applicationClass_ShouldHaveMainMethod() {
        assertDoesNotThrow(() -> {
            DemoApplication.class.getMethod("main", String[].class);
        }, "La classe devrait avoir une méthode main");
    }

    @Test
    void applicationClass_ShouldBePublic() {
        assertTrue(java.lang.reflect.Modifier.isPublic(DemoApplication.class.getModifiers()),
                "La classe DemoApplication devrait être publique");
    }

    @Test
    void mainMethod_ShouldBePublicAndStatic() {
        try {
            var mainMethod = DemoApplication.class.getMethod("main", String[].class);
            assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()),
                    "La méthode main devrait être publique");
            assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()),
                    "La méthode main devrait être statique");
        } catch (NoSuchMethodException e) {
            fail("La méthode main n'existe pas");
        }
    }
}