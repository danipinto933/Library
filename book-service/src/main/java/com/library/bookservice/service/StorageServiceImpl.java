package com.library.bookservice.service;

import com.library.bookservice.exception.ResourceNotFoundException;
import com.library.bookservice.exception.StorageException;
import com.library.bookservice.model.FileData;
import com.library.bookservice.repository.FileDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageServiceImpl implements StorageService {
    private static final Logger log = LoggerFactory.getLogger(StorageServiceImpl.class);

    private final FileDataRepository fileDataRepository;

    public StorageServiceImpl(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

    @Override
    public FileData uploadImageToFileSystem(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Cannot upload empty file");
        }

        String path = "uploads/covers/" + file.getOriginalFilename();
        Path filePath = Paths.get(path);
        
        FileData fileData = fileDataRepository.save(FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(path)
                .build());

        try {
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath);
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to store file {}", file.getOriginalFilename(), e);
            throw new StorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }

        return fileData;
    }

    @Override
    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        FileData fileData = fileDataRepository.findByName(fileName)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con el nombre: " + fileName));
        
        String filePath = fileData.getFilePath();
        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            throw new ResourceNotFoundException("Archivo de imagen físico no encontrado en la ruta: " + filePath);
        }
        
        return Files.readAllBytes(imageFile.toPath());
    }
}
