package io.micronaut.microstream.docs;

import io.micronaut.core.util.StringUtils;

import java.util.Map;

import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class S3CustomerControllerTest extends BaseCustomerControllerTest {

    private static final String BUCKET_NAME = "microstreamcontroller";

    @Override
    protected Map<String, Object> extraProperties() {
        return Map.of(
            "s3.test", StringUtils.TRUE,
            "aws.bucket-name", BUCKET_NAME,
            "microstream.s3.storage.main.bucket-name", BUCKET_NAME,
            "microstream.s3.storage.main.root-class", "io.micronaut.microstream.docs.Data",
            "micronaut.metrics.enabled", StringUtils.FALSE,
            "micronaut.http.client.read-timeout", "60s" // We need to increase this for the localstack object to be created and the image to be pulled
        );
    }

    @EnabledIf("dockerAvailable")
    @ParameterizedTest
    @MethodSource("provideCustomerRepositoryImplementations")
    void testCrud(String customerRepositoryImplementation) throws Exception {
        super.verifyCrudWithMicroStream(customerRepositoryImplementation);
    }
}
