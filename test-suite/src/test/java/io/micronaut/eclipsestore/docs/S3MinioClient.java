package io.micronaut.eclipsestore.docs;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.eclipsestore.testutils.MinioUtils;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URISyntaxException;

@Factory
@Requires(property = "s3.test", value = StringUtils.TRUE)
public class S3MinioClient {

    @Singleton
    @Replaces(S3Client.class)
    S3Client buildClient(S3MinioConfig s3Config) throws URISyntaxException {
        return MinioUtils.s3Client(s3Config);
    }
}
