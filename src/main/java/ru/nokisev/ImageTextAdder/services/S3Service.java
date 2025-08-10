package ru.nokisev.ImageTextAdder.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(S3Client s3Client, @Value("${s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public List<Bucket> returnAllBucketsResponse() {
        List<Bucket> allBuckets = new ArrayList<>();
        String nextToken = null;
        do {
            String token = nextToken;
            ListBucketsResponse response = s3Client.listBuckets(
                    request -> request.continuationToken(token)
            );

            allBuckets.addAll(response.buckets());
            nextToken = response.continuationToken();
        } while (nextToken != null);

        return allBuckets;
    }

    String pattern = "yyyy.MM.dd.n";

    public void saveFileToBucket(File file) {
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)) + "_title")
                .build(), RequestBody.fromFile(file));
        System.out.println("Файл успешно загружен");
    }

}
