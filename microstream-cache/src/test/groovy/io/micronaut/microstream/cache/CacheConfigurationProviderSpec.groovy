package io.micronaut.microstream.cache

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import one.microstream.cache.types.CacheConfiguration
import spock.lang.Specification

import javax.cache.Cache
import javax.cache.configuration.Factory
import javax.cache.expiry.CreatedExpiryPolicy
import javax.cache.expiry.Duration
import javax.cache.expiry.EternalExpiryPolicy
import javax.cache.expiry.ExpiryPolicy

@Property(name = "spec.name", value = "CacheConfigurationProviderSpec")
// Storage properties
@Property(name = "microstream.storage.one.storage-directory", value = 'build/microstream${random.shortuuid}')
// Properties for first cache
@Property(name = "microstream.cache.one.key-type", value = "java.lang.Integer")
@Property(name = "microstream.cache.one.value-type", value = "java.lang.String")
@Property(name = "microstream.cache.one.statistics-enabled", value = "true")
@Property(name = "microstream.cache.one.storage", value = "one")
// Properties for second cache
@Property(name = "microstream.cache.two.key-type", value = "java.lang.Character")
@Property(name = "microstream.cache.two.value-type", value = "java.lang.Float")
@Property(name = "microstream.cache.two.management-enabled", value = "true")
// Properties for third cache
@Property(name = "microstream.cache.three.management-enabled", value = "true")
@MicronautTest(startApplication = false)
class CacheConfigurationProviderSpec extends Specification {

    @Inject
    BeanContext beanContext

    void "you can have multiple beans of type CacheConfigurationProvider"() {
        expect:
        beanContext.getBeansOfType(CacheConfiguration).size() == 3

        when:
        CacheConfiguration oneProvider = beanContext.getBean(CacheConfiguration, Qualifiers.byName("one"))

        then:
        with(oneProvider) {
            keyType == Integer
            valueType == String
            readThrough // When you set a Storage Manager, "read-through" mode is activated.
            writeThrough // When you set a Storage Manager, "write-through" mode is activated.
            !managementEnabled
            statisticsEnabled
            expiryPolicyFactory.create() instanceof EternalExpiryPolicy
        }

        when:
        CacheConfiguration twoProvider = beanContext.getBean(CacheConfiguration, Qualifiers.byName("two"))

        then:
        with(twoProvider) {
            keyType == Character
            valueType == Float
            readThrough // When you set a Storage Manager, "read-through" mode is activated.
            writeThrough // When you set a Storage Manager, "write-through" mode is activated.
            managementEnabled
            !statisticsEnabled
            with(expiryPolicyFactory.create()) {
                it instanceof CreatedExpiryPolicy
                it.expiryForCreation == Duration.FIVE_MINUTES
            }
        }

        when:
        CacheConfiguration threeProvider = beanContext.getBean(CacheConfiguration, Qualifiers.byName("three"))

        then: 'no type specified, so defaults to Object'
        with(threeProvider) {
            keyType == Object
            valueType == Object
            managementEnabled
        }
    }

    @Named("two")
    @Singleton
    @Requires(property = "spec.name", value = "CacheConfigurationProviderSpec")
    static class TwoFiveMinuteExpiryPolicy implements ExpiryPolicyFactory {

        @Override
        Factory<ExpiryPolicy> getFactory() {
            return CreatedExpiryPolicy.factoryOf(Duration.FIVE_MINUTES)
        }
    }

    @Requires(property = "spec.name", value = "CacheConfigurationProviderSpec")
    @Controller
    static class CacheConfigurationProviderController {

        private final Cache<Integer, String> cache

        CacheConfigurationProviderController(@Named("one") Cache<Integer, String> cache) {
            this.cache = cache
        }

        @Get()
        def index() {
            cache.toString()
        }
    }

}
