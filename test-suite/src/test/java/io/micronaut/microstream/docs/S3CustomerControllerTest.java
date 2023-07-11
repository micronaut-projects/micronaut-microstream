package io.micronaut.microstream.docs;

import io.micronaut.core.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import io.micronaut.microstream.testutils.MinioLocal;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class S3CustomerControllerTest extends BaseCustomerControllerTest {

    private static final String BUCKET_NAME = "microstreamcontroller";

    @Container
    MinioLocal minioLocal = new MinioLocal();

    @Override
    protected Map<String, Object> extraProperties() {
        var properties = new HashMap<>(minioLocal.getProperties());
        properties.putAll(Map.of(
            "s3.test", StringUtils.TRUE,
            "aws.bucket-name", BUCKET_NAME,
            "microstream.s3.storage.main.bucket-name", BUCKET_NAME,
            "microstream.s3.storage.main.root-class", "io.micronaut.microstream.docs.Data",
            "micronaut.metrics.enabled", StringUtils.FALSE
        ));
        return properties;
    }

    @EnabledIf("dockerAvailable")
    @ParameterizedTest
    @MethodSource("provideCustomerRepositoryImplementations")
    void testCrud(String customerRepositoryImplementation) throws Exception {
        super.verifyCrudWithMicroStream(customerRepositoryImplementation);
    }
}
