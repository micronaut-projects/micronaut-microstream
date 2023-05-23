package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;

@ConfigurationProperties("aws")
@Requires(property = "s3.test", value = StringUtils.TRUE)
class S3LocalstackConfig {
    private String accessKeyId;
    private String secretKey;
    private String region;
    private String testBucketName;

    @ConfigurationBuilder(configurationPrefix = "services.s3")
    final S3 s3 = new S3();

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public S3 getS3() {
        return s3;
    }

    public String getTestBucketName() {
        return testBucketName;
    }

    public void setTestBucketName(String testBucketName) {
        this.testBucketName = testBucketName;
    }

    static class S3 {
        String endpointOverride;

        public String getEndpointOverride() {
            return endpointOverride;
        }

        public void setEndpointOverride(String endpointOverride) {
            this.endpointOverride = endpointOverride;
        }
    }
}
