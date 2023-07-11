package io.micronaut.microstream.testutils;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.net.URI;
import java.net.URISyntaxException;

public final class MinioUtils {

    private MinioUtils() {
    }

    public static S3Client s3Client(S3ConfigurationProperties s3Config) throws URISyntaxException {
        S3Client client = S3Client.builder()
            .endpointOverride(new URI(s3Config.getS3Configuration().getEndpointOverride()))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3Config.getAccessKeyId(), s3Config.getSecretKey())))
            .region(Region.of(s3Config.getRegion()))
            .serviceConfiguration(b -> b.pathStyleAccessEnabled(true)) // Required for minio
            .build();
        if (client.listBuckets().buckets().stream().map(Bucket::name).noneMatch(s3Config.getBucketName()::equals)) {
            client.createBucket(b -> b.bucket(s3Config.getBucketName()));
        }
        return client;
    }
}
