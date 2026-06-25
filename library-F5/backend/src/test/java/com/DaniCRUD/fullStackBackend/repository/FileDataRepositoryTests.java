package com.DaniCRUD.fullStackBackend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.DaniCRUD.fullStackBackend.model.FileData;

@DataJpaTest
public class FileDataRepositoryTests {

    @Autowired
    private FileDataRepository fileDataRepository;

    private FileData fileData1;

    @BeforeEach
    void setup() {
        System.out.println("Ejecutando tests de repository de FileData desde cero...");
        fileData1 = FileData.builder()
                .name("test-image.jpg")
                .type("image/jpeg")
                .filePath("uploads/covers/test-image.jpg")
                .build();
    }

    @Test
    void testSaveFileData() {
        FileData saved = fileDataRepository.save(fileData1);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("test-image.jpg", saved.getName());
    }

    @Test
    void testFindByName() {
        fileDataRepository.save(fileData1);

        Optional<FileData> found = fileDataRepository.findByName("test-image.jpg");
        assertTrue(found.isPresent());
        assertEquals("test-image.jpg", found.get().getName());
    }

    @Test
    void testFindByName_NotFound() {
        Optional<FileData> found = fileDataRepository.findByName("non-existent.jpg");
        assertTrue(found.isEmpty());
    }

    @Test
    void testDeleteFileData() {
        FileData saved = fileDataRepository.save(fileData1);
        assertNotNull(saved);

        fileDataRepository.delete(saved);
        Optional<FileData> found = fileDataRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }
}
