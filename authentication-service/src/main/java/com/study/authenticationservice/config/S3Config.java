package com.study.authenticationservice.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j

public class S3Config {
    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    /**
     * This method creates and returns an `AmazonS3` bean for use in the application.
     *
     * The bean is configured with AWS credentials, including the `accessKey` and `secretKey`,
     * as well as the region where the S3 service is located.
     *
     * The method uses:
     * - `BasicAWSCredentials`: An object containing the AWS Access Key and Secret Key
     *   to authenticate with Amazon S3.
     * - `AmazonS3Client.builder()`: A builder that helps create an `AmazonS3Client`
     *   with the required configurations.
     * - `withRegion(region)`: Specifies the AWS region where the S3 bucket is stored.
     * - `withCredentials(new AWSStaticCredentialsProvider(credentials))`: Provides
     *   the credentials for connecting.
     *
     * After creating and configuring the client, the method returns an `AmazonS3` object,
     * which can be used by other services to perform operations on Amazon S3, such as
     * uploading, downloading, or deleting files.
     *
     * @return a configured `AmazonS3` object.
     */
    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3Client.builder()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
