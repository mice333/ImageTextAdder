package ru.nokisev.ImageTextAdder.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final StringRedisTemplate redisTemplate;

    public S3Service(S3Client s3Client, @Value("${s3.bucket-name}") String bucketName, StringRedisTemplate redisTemplate) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.redisTemplate = redisTemplate;
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

    public void saveFileToBucket(File file, Long id) {
        log.info("Сохранение изображение в Redis");
        redisTemplate.opsForValue().set(String.valueOf(id), getImageLink(String.valueOf(id)), 3,TimeUnit.MINUTES);

        log.info("Загрузка изображения в S3");
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(id.toString())
                .build(), RequestBody.fromFile(file));
        log.info("Изображение успешно сохранено");
    }

    public String getImageLink(String id) {
        log.info("Передача изображения по API");
        return "https://bdc55126-63c8-4c3f-aa9a-1abaaadf1fba.selstorage.ru/" + id;
    }

}
