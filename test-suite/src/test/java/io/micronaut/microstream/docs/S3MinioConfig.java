package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.microstream.testutils.S3Configuration;
import io.micronaut.microstream.testutils.S3ConfigurationProperties;

@ConfigurationProperties("aws")
@Requires(property = "s3.test", value = StringUtils.TRUE)
class S3MinioConfig extends S3ConfigurationProperties {
    @ConfigurationBuilder(configurationPrefix = "services.s3")
    S3Configuration s3 = new S3Configuration();

    @Override
    public S3Configuration getS3Configuration() {
        return this.s3;
    }
}
