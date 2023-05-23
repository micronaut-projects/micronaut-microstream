package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;

@Factory
@Requires(property = "s3.test", value = StringUtils.TRUE)
public class S3LocalstackClient {

    private final S3LocalstackConfig s3Config;

    S3LocalstackClient(S3LocalstackConfig s3Config) {
        this.s3Config = s3Config;
    }

    @Singleton
    @Replaces(S3Client.class)
    S3Client buildClient() throws URISyntaxException, InterruptedException {
        S3Client client = S3Client.builder()
            .endpointOverride(new URI(s3Config.s3.endpointOverride))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3Config.getAccessKeyId(), s3Config.getSecretKey())))
            .region(Region.of(s3Config.getRegion()))
            .build();
        client.createBucket(b -> b.bucket(s3Config.getTestBucketName()));
        try {
            // localstack seems to require one of these that fails before the next one (from MicroStream) passes. I have no idea why.
            client.putObject(b -> b.bucket(s3Config.getTestBucketName()).key("/"), RequestBody.empty());
        } catch (Exception e) {
        }
        return client;
    }
}
