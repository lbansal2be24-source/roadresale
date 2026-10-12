package com.roadresale.service;

import org.springframework.web.multipart.MultipartFile;

// Where do uploaded photos go? The rest of the app does not care.
// Right now: LocalStorageService (a folder on this computer).
// Later on AWS: an S3StorageService that implements this same interface.
public interface StorageService {

    // Saves the photo and returns the web address that shows it.
    String save(MultipartFile photo);
}
