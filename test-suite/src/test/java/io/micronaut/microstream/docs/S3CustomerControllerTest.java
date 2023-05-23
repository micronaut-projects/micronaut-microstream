package io.micronaut.microstream.docs;

import io.micronaut.core.util.StringUtils;

import java.util.Map;

class S3CustomerControllerTest extends BaseCustomerControllerTest {

    private static final String BUCKET_NAME = "microstreamcontroller";

    @Override
    protected Map<String, Object> extraProperties() {
        return Map.of(
            "s3.test", StringUtils.TRUE,
            "aws.test-bucket-name", BUCKET_NAME,
            "microstream.s3.storage.main.bucket-name", BUCKET_NAME,
            "microstream.s3.storage.main.root-class", "io.micronaut.microstream.docs.Data",
            "micronaut.metrics.enabled", StringUtils.FALSE,
            "micronaut.http.client.read-timeout", "60s" // We need to increase this for the localstack object to be created and the image to be pulled
        );
    }
}
