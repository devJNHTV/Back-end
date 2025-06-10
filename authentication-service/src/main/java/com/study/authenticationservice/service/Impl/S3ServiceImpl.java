package com.study.authenticationservice.service.Impl;

import com.study.authenticationservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Service
public class S3ServiceImpl implements S3Service {

    @Autowired
    private  AmazonS3 amazonS3;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) throws Exception, IOException {
        String key = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), metadata);

            amazonS3.putObject(putObjectRequest);
            log.info("Upload amazon s3 success - key={}", key);

            return amazonS3.getUrl(bucketName, key).toString();
        } catch (Exception ex) {
            log.error("Upload amazon s3 failed - key={} - cause = {}", key, ex.getMessage());
            throw new IOException(ex);
        }
    }

    @Override
    public void deleteFile(String url) throws Exception {
        try {
            String key = url.substring(url.lastIndexOf("/") + 1);
            amazonS3.deleteObject(bucketName, key);
            log.info("File deleted successfully from S3 - key={}", key);
        } catch (Exception ex) {
            log.error("Failed to delete file from S3 - cause = {}", ex.getMessage());
            throw new Exception("Failed to delete file from S3", ex);
        }
    }

    @Override
    public String downloadFile(String url) throws Exception {
        String key = url.substring(url.lastIndexOf("/") + 1);
        String localFilePath = "path/to/save/" + key; // Specify your path here

        try {
            S3Object s3Object = amazonS3.getObject(bucketName, key);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            try (FileOutputStream outputStream = new FileOutputStream(localFilePath)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }

            log.info("File downloaded successfully: " + localFilePath);
            return localFilePath;

        } catch (IOException e) {
            log.error("Failed to download file from S3: " + e.getMessage());
            throw new Exception("Failed to download file from S3", e);
        }
    }
}
