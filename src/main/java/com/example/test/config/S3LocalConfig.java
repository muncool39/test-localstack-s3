package com.example.test.config;



import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Profile(value = "local")
@Configuration
public class S3LocalConfig {
    public static final String BUCKET_NAME = "local-bucket";

    private final String AWS_REGION = Regions.US_EAST_1.getName();
    private final String AWS_ENDPOINT = "http://127.0.0.1:4566";

    @Value("cloud.aws.s3.access-key")
    private String accessKey;
    @Value("cloud.aws.s3.secret-key")
    private String secretKey;

    @Bean
    public AmazonS3 amazonS3() {
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(AWS_ENDPOINT, AWS_REGION);
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        amazonS3.createBucket(BUCKET_NAME);
        return amazonS3;
    }

}
