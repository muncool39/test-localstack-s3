package com.example.test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import cloud.localstack.ServiceName;
import cloud.localstack.awssdkv1.TestUtils;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.services.s3.AmazonS3;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

@Slf4j
@ExtendWith(LocalstackDockerExtension.class)
@LocalstackDockerProperties(services = ServiceName.S3)
public class LocalStackDockerExtensionTest {
    @Test
    void test() throws Exception {
        AmazonS3 s3 = TestUtils.getClientS3();

        String bucketName = "test-s3";
        s3.createBucket(bucketName);
        log.info("버킷을 생성했습니다. bucketName = {}", bucketName);

        String content = "Hello World";
        String key = "s3-key";
        s3.putObject(bucketName, key, content);
        log.info("파일을 업로드하였습니다. bucketName = {}, key = {}, content = {}", bucketName, key, content);

        List<String> results = IOUtils.readLines(s3.getObject(bucketName, key).getObjectContent(), "utf-8");
        log.info("파일을 가져왔습니다. bucketName = {}, key = {}, results = {}", bucketName, key, results);

        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(content);
    }
}
