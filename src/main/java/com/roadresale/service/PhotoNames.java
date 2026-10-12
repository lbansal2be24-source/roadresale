package com.roadresale.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

// Small helper shared by LocalStorageService and S3StorageService.
public class PhotoNames {

    // Checks the photo is a JPG/PNG and returns a new random file name for it,
    // e.g. "my car.JPG" -> "3f2a9c1e-....jpg". Random names can never clash.
    public static String newFileName(MultipartFile photo) {
        String original = photo.getOriginalFilename();
        String extension = "";
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf(".")).toLowerCase();
        }
        if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png")) {
            throw new IllegalArgumentException("Only JPG or PNG photos are allowed.");
        }
        return UUID.randomUUID() + extension;
    }
}
