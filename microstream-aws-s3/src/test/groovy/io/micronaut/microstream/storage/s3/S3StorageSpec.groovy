package io.micronaut.microstream.storage.s3

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation
import one.microstream.storage.types.StorageManager
import software.amazon.awssdk.services.s3.S3Client
import spock.lang.Specification;


@Property(name = "microstream.s3.storage.foo.bucket-name", value = "microstreamfoo")
@Property(name = "microstream.s3.storage.foo.root-class", value = "io.micronaut.microstream.s3.Root")
@MicronautTest
class S3StorageSpec extends Specification {


    @Inject
    BeanContext beanContext

    void "expected beans are created"() {
        expect:
        beanContext.containsBean(S3StorageConfigurationProvider, Qualifiers.byName("foo"))
        !beanContext.containsBean(S3Client, Qualifiers.byName("foo"))
        beanContext.containsBean(S3Client)
        beanContext.containsBean(EmbeddedStorageFoundation, Qualifiers.byName("foo"))
        beanContext.getBeansOfType(StorageManager).size() == 1

        when:
        beanContext.getBean(CustomerRepository)

        then:
        noExceptionThrown()
    }

    @Singleton
    static class CustomerRepository {

        private final StorageManager storageManager

        CustomerRepository(StorageManager storageManager) {
            this.storageManager = storageManager
        }
    }
}
