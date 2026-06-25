package com.DaniCRUD.fullStackBackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImageControllerTestRestTemplateTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${IMAGE_PATH:uploads/covers/}")
    private String imagePath;

    @Test
    void testServeImage() throws Exception {
        Path targetPath = Paths.get(imagePath).resolve("test-serve-rest.jpg").normalize();
        File testFile = targetPath.toFile();
        
        testFile.getParentFile().mkdirs();
        Files.write(testFile.toPath(), "rest content".getBytes());

        try {
            ResponseEntity<byte[]> response = testRestTemplate.getForEntity(
                    "/uploads/covers/test-serve-rest.jpg",
                    byte[].class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("rest content", new String(response.getBody()));
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }

    @Test
    void testServeImage_NotFound() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                "/uploads/covers/non-existent-rest.jpg",
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
