package io.micronaut.microstream.cache

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import spock.lang.Specification

import javax.cache.configuration.Factory
import javax.cache.expiry.CreatedExpiryPolicy
import javax.cache.expiry.Duration
import javax.cache.expiry.EternalExpiryPolicy
import javax.cache.expiry.ExpiryPolicy

@Property(name = "spec.name", value = "CacheConfigurationProviderSpec")
@Property(name = "microstream.cache.one.key-type", value = "java.lang.Integer")
@Property(name = "microstream.cache.one.value-type", value = "java.lang.String")
@Property(name = "microstream.cache.two.key-type", value = "java.lang.Character")
@Property(name = "microstream.cache.two.value-type", value = "java.lang.Float")
@MicronautTest(startApplication = false)
class CacheConfigurationProviderSpec extends Specification {

    @Inject
    BeanContext beanContext

    void "you can have multiple beans of type CacheConfigurationProvider"() {
        expect:
        beanContext.getBeansOfType(CacheConfigurationProvider).size() == 2

        when:
        CacheConfigurationProvider oneProvider = beanContext.getBean(CacheConfigurationProvider, Qualifiers.byName("one"))

        then:
        oneProvider.name == 'one'
        with(oneProvider.builder.build()) {
            keyType == Integer
            valueType == String
            !readThrough
            !writeThrough
            !managementEnabled
            !statisticsEnabled
            expiryPolicyFactory.create() instanceof EternalExpiryPolicy
        }

        when:
        CacheConfigurationProvider twoProvider = beanContext.getBean(CacheConfigurationProvider, Qualifiers.byName("two"))

        then:
        twoProvider.name == 'two'
        with(twoProvider.builder.build()) {
            keyType == Character
            valueType == Float
            !readThrough
            !writeThrough
            !managementEnabled
            !statisticsEnabled
            with(expiryPolicyFactory.create()) {
                it instanceof CreatedExpiryPolicy
                it.expiryForCreation == Duration.FIVE_MINUTES
            }
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
}
