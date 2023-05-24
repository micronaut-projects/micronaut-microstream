package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.microstream.testutils.LocalStackUtils;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URISyntaxException;

@Factory
@Requires(property = "s3.test", value = StringUtils.TRUE)
public class S3LocalstackClient {

    @Singleton
    @Replaces(S3Client.class)
    S3Client buildClient(S3LocalstackConfig s3Config) throws URISyntaxException {
        return LocalStackUtils.s3Client(s3Config);
    }
}
