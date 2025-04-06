package com.tellem.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class AwsS3Service {
    private final AmazonS3 s3Client;
    private final String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
    private final String secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
    private final String bucketName = System.getenv("AWS_S3_BUCKET_NAME");


    public AwsS3Service() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getOriginalFilename(), inputStream, metadata);
        s3Client.putObject(putObjectRequest);
        return s3Client.getUrl(bucketName, file.getOriginalFilename()).toString();
    }
}

