package com.example.test;





import static com.example.test.config.S3LocalConfig.BUCKET_NAME;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

@RestController
@RequiredArgsConstructor
public class S3TestController {
    private final AmazonS3 amazonS3;

    @GetMapping("/download")
    public ResponseEntity<byte[]> get(@RequestParam String fileName) throws IOException {
        S3Object s3Object = amazonS3.getObject(BUCKET_NAME, fileName);
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam MultipartFile file) {
        File uploadFile = convert(file);
        uploadToS3(uploadFile);
        return ResponseEntity.ok(null);
    }

    private File convert(MultipartFile multipartFile) {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return file;
    }

    private void uploadToS3(File uploadFile) {
        try {
            amazonS3.putObject(BUCKET_NAME, uploadFile.getName(), uploadFile);
        } catch (SdkClientException e) {
            throw new RuntimeException();
        } finally {
            removeNewFile(uploadFile);
        }
    }

    private void removeNewFile(File uploadFile) {
        uploadFile.delete();
    }
}
