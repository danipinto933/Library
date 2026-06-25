package com.DaniCRUD.fullStackBackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.exception.StorageException;
import com.DaniCRUD.fullStackBackend.model.FileData;
import com.DaniCRUD.fullStackBackend.repository.FileDataRepository;

@ExtendWith(MockitoExtension.class)
public class StorageServiceTests {

    @Mock
    private FileDataRepository fileDataRepository;

    @InjectMocks
    private StorageServiceImpl storageServiceImpl;

    private FileData fileData1;

    @BeforeEach
    void setup() {
        fileData1 = FileData.builder()
                .id(1L)
                .name("test-file.jpg")
                .type("image/jpeg")
                .filePath("uploads/covers/test-file.jpg")
                .build();
    }

    @Test
    void testUploadImageToFileSystem() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test-upload.jpg", "image/jpeg", "file content".getBytes());
        
        given(fileDataRepository.save(any(FileData.class))).willReturn(fileData1);

        File fileOnDisk = new File("uploads/covers/test-upload.jpg");
        try {
            FileData result = storageServiceImpl.uploadImageToFileSystem(mockFile);
            assertNotNull(result);
            verify(fileDataRepository).save(any(FileData.class));
        } finally {
            if (fileOnDisk.exists()) {
                fileOnDisk.delete();
            }
        }
    }

    @Test
    void testUploadImageToFileSystem_EmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);

        assertThrows(StorageException.class, () -> storageServiceImpl.uploadImageToFileSystem(emptyFile));
        verify(fileDataRepository, never()).save(any(FileData.class));
    }

    @Test
    void testDownloadImageFromFileSystem() throws IOException {
        File testFile = new File("uploads/covers/test-download.jpg");
        testFile.getParentFile().mkdirs();
        Files.write(testFile.toPath(), "file content".getBytes());

        FileData fileData = FileData.builder()
                .name("test-download.jpg")
                .filePath("uploads/covers/test-download.jpg")
                .build();

        given(fileDataRepository.findByName("test-download.jpg")).willReturn(Optional.of(fileData));

        try {
            byte[] result = storageServiceImpl.downloadImageFromFileSystem("test-download.jpg");
            assertNotNull(result);
            assertEquals("file content", new String(result));
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }

    @Test
    void testDownloadImageFromFileSystem_NotFoundInDb() {
        given(fileDataRepository.findByName("non-existent.jpg")).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> storageServiceImpl.downloadImageFromFileSystem("non-existent.jpg"));
    }

    @Test
    void testDownloadImageFromFileSystem_FileNotFoundOnDisk() {
        given(fileDataRepository.findByName("test-file.jpg")).willReturn(Optional.of(fileData1));

        // Nos aseguramos de que el archivo físico no exista
        File physicalFile = new File(fileData1.getFilePath());
        if (physicalFile.exists()) {
            physicalFile.delete();
        }

        assertThrows(ResourceNotFoundException.class, () -> storageServiceImpl.downloadImageFromFileSystem("test-file.jpg"));
    }
}
