package com.roadresale.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Saves photos into a folder on this computer (the "uploads" folder).
// Used when application.properties says app.storage=local (also the default).
@Service
@ConditionalOnProperty(name = "app.storage", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {
    private final Path uploadFolder;

    public LocalStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadFolder = Paths.get(uploadDir).toAbsolutePath();
    }

    @Override
    public String save(MultipartFile photo) {
        String fileName = PhotoNames.newFileName(photo);
        try {
            Files.createDirectories(uploadFolder);
            photo.transferTo(uploadFolder.resolve(fileName));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not save the photo. Please try again.");
        }
        return "/uploads/" + fileName;
    }
}
