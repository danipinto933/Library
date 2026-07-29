package com.library.bookservice.service;

import com.library.bookservice.model.FileData;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {
    FileData uploadImageToFileSystem(MultipartFile file);
    byte[] downloadImageFromFileSystem(String fileName) throws IOException;
}
