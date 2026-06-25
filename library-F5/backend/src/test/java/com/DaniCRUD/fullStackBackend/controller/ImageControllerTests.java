package com.DaniCRUD.fullStackBackend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ImageController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
public class ImageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${IMAGE_PATH:uploads/covers/}")
    private String imagePath;

    @Test
    void testServeImage() throws Exception {
        Path targetPath = Paths.get(imagePath).resolve("test-serve-unit.jpg").normalize();
        File testFile = targetPath.toFile();
        
        // Crear directorios y archivo
        testFile.getParentFile().mkdirs();
        Files.write(testFile.toPath(), "content".getBytes());

        try {
            mockMvc.perform(get("/uploads/covers/test-serve-unit.jpg"))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes("content".getBytes()));
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }

    @Test
    void testServeImage_NotFound() throws Exception {
        mockMvc.perform(get("/uploads/covers/non-existent-unit.jpg"))
                .andExpect(status().isNotFound());
    }
}
