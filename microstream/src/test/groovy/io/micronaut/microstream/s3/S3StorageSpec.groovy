package io.micronaut.microstream.s3

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.*
import io.micronaut.core.util.StringUtils
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.microstream.BaseStorageSpec
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation
import one.microstream.storage.types.StorageManager
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import spock.lang.Shared

@MicronautTest
@Property(name = "microstream.s3.storage.foo.bucket-name", value = S3StorageSpec.BUCKET_NAME)
@Property(name = "microstream.s3.storage.foo.root-class", value = 'io.micronaut.microstream.BaseStorageSpec$Root')
@Property(name = "micronaut.metrics.enabled", value = StringUtils.FALSE)
@Property(name = "spec.type", value = "storage")
@Property(name = "spec.name", value = "S3StorageSpec")
class S3StorageSpec extends BaseStorageSpec {

    static final String BUCKET_NAME = "microstreamfoo"

    @Inject
    BeanContext beanContext

    @Inject
    @Shared
    S3Client client

    @Inject
    CustomerRepository customerRepository

    def cleanup() {
        client.listObjectsV2 { it.bucket(BUCKET_NAME) }.contents().each { o ->
            println "Deleting ${o.key()}"
            client.deleteObject { it.bucket(BUCKET_NAME).key(o.key()) }
        }
    }

    void "expected beans are created"() {
        expect:
        beanContext.containsBean(S3StorageConfigurationProvider, Qualifiers.byName("foo"))
        !beanContext.containsBean(S3Client, Qualifiers.byName("foo"))
        beanContext.containsBean(S3Client)
        beanContext.containsBean(EmbeddedStorageFoundation, Qualifiers.byName("foo"))
        beanContext.getBeansOfType(StorageManager).size() == 1

        when:
        customerRepository.updateName("foo")

        then:
        customerRepository.name() == "foo"

        when:
        beanContext.getBean(CustomerRepository)

        then:
        noExceptionThrown()
    }

    // Use localstack config for the client

    @Factory
    @Requires(property = "spec.name", value = "S3StorageSpec")
    static class LocalStackClient {

        S3Config s3Config

        LocalStackClient(S3Config s3Config) {
            this.s3Config = s3Config
        }

        @Replaces(S3Client)
        @Singleton
        S3Client buildClient() {
            def client = S3Client.builder()
                    .endpointOverride(new URI(s3Config.s3.endpointOverride))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3Config.accessKeyId, s3Config.secretKey)))
                    .region(Region.of(s3Config.region))
                    .build()
            client.createBucket { it.bucket(BUCKET_NAME) }
            try {
                // localstack seems to require one of these that fails before the next one (from MicroStream) passes. I have no idea why.
                client.putObject({ it.bucket(BUCKET_NAME).key("/") }, RequestBody.empty())
            } catch (e) {
                e.printStackTrace()
            }
            client
        }
    }

    @ConfigurationProperties("aws")
    @Requires(property = "spec.name", value = "S3StorageSpec")
    static class S3Config {
        String accessKeyId
        String secretKey
        String region

        @ConfigurationBuilder(configurationPrefix = "services.s3")
        final S3 s3 = new S3()

        static class S3 {
            String endpointOverride
        }
    }
}
