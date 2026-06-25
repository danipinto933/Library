package com.DaniCRUD.fullStackBackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ImageControllerWebTestClientTests {

    @Autowired
    private WebTestClient webTestClient;

    @Value("${IMAGE_PATH:uploads/covers/}")
    private String imagePath;

    @Test
    void testServeImage() throws Exception {
        Path targetPath = Paths.get(imagePath).resolve("test-serve-web.jpg").normalize();
        File testFile = targetPath.toFile();
        
        testFile.getParentFile().mkdirs();
        Files.write(testFile.toPath(), "web content".getBytes());

        try {
            webTestClient.get()
                    .uri("/uploads/covers/test-serve-web.jpg")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(byte[].class)
                    .value(bytes -> {
                        assertEquals("web content", new String(bytes));
                    });
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }

    @Test
    void testServeImage_NotFound() {
        webTestClient.get()
                .uri("/uploads/covers/non-existent-web.jpg")
                .exchange()
                .expectStatus().isNotFound();
    }
}
