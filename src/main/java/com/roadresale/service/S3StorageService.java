package com.roadresale.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

// Saves photos into an AWS S3 bucket.
// Used when application.properties says app.storage=s3.
// Same interface as LocalStorageService, so AdService does not change at all.
@Service
@ConditionalOnProperty(name = "app.storage", havingValue = "s3")
public class S3StorageService implements StorageService {
    private final String bucket;
    private final String region;
    private final S3Client s3;

    public S3StorageService(@Value("${app.s3.bucket}") String bucket,
                            @Value("${app.s3.region}") String region) {
        this.bucket = bucket;
        this.region = region;
        // No access keys in the code: on EC2 the SDK automatically uses the
        // IAM role attached to the server.
        this.s3 = S3Client.builder().region(Region.of(region)).build();
    }

    @Override
    public String save(MultipartFile photo) {
        String fileName = "photos/" + PhotoNames.newFileName(photo);
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(photo.getContentType())
                    .build();
            s3.putObject(request, RequestBody.fromInputStream(photo.getInputStream(), photo.getSize()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not save the photo. Please try again.");
        }
        // The public web address of the photo in the bucket.
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }
}
