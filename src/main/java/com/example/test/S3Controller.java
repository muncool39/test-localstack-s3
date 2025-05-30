package com.example.test;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;

    @GetMapping("/url")
    public ResponseEntity<String> getFileUrl(@RequestParam String fileName) {
        return ResponseEntity.ok(s3Service.getFileUrlFromS3(fileName));
    }

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(MultipartFile file) {
        return ResponseEntity.ok(s3Service.uploadFileAndGetUrl(file));
    }

}
