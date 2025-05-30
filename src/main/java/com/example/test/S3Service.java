package com.example.test;


import static com.example.test.config.S3LocalConfig.BUCKET_NAME;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    private final int S3_PATH_LENGTH = 10;


    public String uploadFileAndGetUrl(final MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        String fileName = getRandomFileName(file);
        try {
            amazonS3.putObject(
                    new PutObjectRequest(BUCKET_NAME, fileName, file.getInputStream(), metadata)
            );
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return getFileUrlFromS3(fileName);
    }

    public void deleteFileFromS3(final String fileUrl){
        amazonS3.deleteObject(BUCKET_NAME, fileUrl.substring(S3_PATH_LENGTH));
    }

    public String getFileUrlFromS3(final String fileName) {
        return amazonS3.getUrl(BUCKET_NAME, fileName).toString();
    }

    private String getRandomFileName(final MultipartFile file) {
        String randomUUID = UUID.randomUUID().toString();
        return randomUUID + file.getOriginalFilename();
    }
}
