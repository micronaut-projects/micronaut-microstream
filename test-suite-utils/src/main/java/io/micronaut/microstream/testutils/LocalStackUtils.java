package io.micronaut.microstream.testutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;

public final class LocalStackUtils {
    private static final Logger LOG = LoggerFactory.getLogger(LocalStackUtils.class);

    private LocalStackUtils() {
    }
    public static S3Client s3Client(S3ConfigurationProperties s3Config) throws URISyntaxException {
        S3Client client = S3Client.builder()
            .endpointOverride(new URI(s3Config.getS3Configuration().getEndpointOverride()))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3Config.getAccessKeyId(), s3Config.getSecretKey())))
            .region(Region.of(s3Config.getRegion()))
            .build();
        client.createBucket(b -> b.bucket(s3Config.getBucketName()));
        try {
            // localstack seems to require one of these that fails before the next one (from MicroStream) passes. I have no idea why.
            client.putObject(b -> b.bucket(s3Config.getBucketName()).key("/"), RequestBody.empty());
        } catch (Exception e) {
            LOG.error("exception creating a dummy bucket", e);
        }
        return client;
    }
}
