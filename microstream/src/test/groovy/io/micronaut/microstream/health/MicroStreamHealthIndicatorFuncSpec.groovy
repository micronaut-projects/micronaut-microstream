package io.micronaut.microstream.health

import io.micronaut.context.BeanContext
import io.micronaut.health.HealthStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Named
import one.microstream.storage.types.StorageManager
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Unroll

@MicronautTest(startApplication = false)
class MicroStreamHealthIndicatorFuncSpec extends Specification {

    @Inject
    BeanContext beanContext

    StorageManager mockStorageManager = Mock()

    @Unroll
    void "#desc manager is #expectedStatus"() {
        when:
        MicroStreamHealthIndicator healthIndicator = beanContext.getBean(MicroStreamHealthIndicator)
        StepVerifier.create(healthIndicator.result)
                .expectNextMatches(t -> t.status == expectedStatus)
                .verifyComplete()
        then:
        _ * mockStorageManager.isRunning() >> isRunning

        where:
        expectedStatus    | isRunning | desc
        HealthStatus.DOWN | false     | 'not running'
        HealthStatus.UP   | true      | 'running'
    }

    @MockBean(bean = StorageManager, named = "mock-manager")
    @Named("mock-manager")
    StorageManager getStorageManager() {
        mockStorageManager
    }
}
