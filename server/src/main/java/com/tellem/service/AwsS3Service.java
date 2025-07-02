package com.tellem.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class AwsS3Service {
    private final AmazonS3 s3Client;
    private final String bucketName = System.getenv("AWS_S3_BUCKET_NAME");


    public AwsS3Service() {
        String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        String secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
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

    public List<String> getAllImagesFromBucket() {
        List<String> imageUrls = new ArrayList<>();
        ObjectListing objectListing = s3Client.listObjects("tell-em-bucket");

        for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {
            String key = summary.getKey();
            if (key.endsWith(".jpg") || key.endsWith(".png") || key.endsWith(".jpeg")) {
                String url = "https://tell-em-bucket.s3.eu-central-1.amazonaws.com/" + key;
                imageUrls.add(url);
            }
        }

        return imageUrls;
    }
}

