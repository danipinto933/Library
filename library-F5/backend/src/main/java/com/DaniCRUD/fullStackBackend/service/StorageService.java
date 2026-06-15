package com.DaniCRUD.fullStackBackend.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.DaniCRUD.fullStackBackend.model.FileData;

@Service
public interface StorageService
{
    FileData uploadImageToFileSystem(MultipartFile file);

    byte[] downloadImageFromFileSystem(String fileName) throws IOException;
}
