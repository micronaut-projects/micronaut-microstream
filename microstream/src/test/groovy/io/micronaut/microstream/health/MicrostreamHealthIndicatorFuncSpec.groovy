package io.micronaut.microstream.health

import io.micronaut.context.BeanContext
import io.micronaut.health.HealthStatus
import io.micronaut.management.health.indicator.HealthResult
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Named
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Unroll
import java.util.function.Consumer

@MicronautTest(startApplication = false)
class MicrostreamHealthIndicatorFuncSpec extends Specification {

    @Inject
    BeanContext beanContext

    EmbeddedStorageManager mockStorageManager = Mock()

    @Unroll
    void "#desc manager is #expectedStatus"() {
        when:
        MicrostreamHealthIndicator healthIndicator = beanContext.getBean(MicrostreamHealthIndicator)
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

    @MockBean(bean = EmbeddedStorageManager, named = "mock-manager")
    @Named("mock-manager")
    EmbeddedStorageManager getEmbeddedStorageManager() {
        mockStorageManager
    }
}
