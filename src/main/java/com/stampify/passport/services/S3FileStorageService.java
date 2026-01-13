package com.stampify.passport.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class S3FileStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public S3FileStorageService(@Value("${aws.s3.region}") String region) {
        this.region = region;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Upload a generic file to a specific folder in S3
     * Files will be publicly accessible via bucket policy
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String fileName = URLEncoder.encode(file.getOriginalFilename(), StandardCharsets.UTF_8);
        String key = folder + "/" + System.currentTimeMillis() + "_" + fileName;

        // Remove ACL since bucket enforces ownership
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

        // Public URL (works because bucket policy allows s3:GetObject for everyone)
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

    public String uploadVenueImage(MultipartFile file) throws IOException {
        return uploadFile(file, "events/images");
    }

    public String uploadEventBadge(MultipartFile file) throws IOException {
        return uploadFile(file, "events/badges");
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        String key = fileUrl.substring(fileUrl.indexOf(".amazonaws.com/") + 15);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    public void deleteVenueImage(String url) {
        deleteFile(url);
    }

    public void deleteEventBadge(String url) {
        deleteFile(url);
    }
}
