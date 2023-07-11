package io.micronaut.microstream.testutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.net.URI;
import java.net.URISyntaxException;

public final class LocalStackUtils {
    private static final Logger LOG = LoggerFactory.getLogger(LocalStackUtils.class);

    private LocalStackUtils() {
    }

    @SuppressWarnings("java:S2142")
    public static S3Client s3Client(S3ConfigurationProperties s3Config) throws URISyntaxException {
        LOG.info("creating s3 client with endpoint {}, accessKey {} and secret {} for region {}",
                s3Config.getS3Configuration().getEndpointOverride(),
                s3Config.getAccessKeyId(),
                s3Config.getSecretKey(),
                s3Config.getRegion()
        );
        S3Client client = S3Client.builder()
            .endpointOverride(new URI(s3Config.getS3Configuration().getEndpointOverride()))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3Config.getAccessKeyId(), s3Config.getSecretKey())))
            .region(Region.of(s3Config.getRegion()))
            .build();
        client.createBucket(b -> b.bucket(s3Config.getBucketName()));
        // Localstack needs some time to sort the bucket out it seems especially on CI
        LOG.info("buckets {}", client.listBuckets().buckets());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        try {
            // And then localstack seems to require one of these that fails before the next one (from MicroStream) passes. I have no idea why.
            PutObjectResponse putObjectResponse = client.putObject(b -> b.bucket(s3Config.getBucketName()).key("/"), RequestBody.empty());
            LOG.info("put object response {}", putObjectResponse);
        } catch (Exception e) {
            LOG.error("caught error putting object at /");
        }
        return client;
    }
}
