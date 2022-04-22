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
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.util.function.Consumer

@MicronautTest(startApplication = false)
class MicrostreamHealthIndicatorFuncSpec extends Specification {

    @Inject
    BeanContext beanContext

    EmbeddedStorageManager mockStorageManager = Mock()

    void "#desc manager is #expectedStatus"() {
        given:
        MicrostreamHealthIndicator healthIndicator = beanContext.getBean(MicrostreamHealthIndicator)
        List<HealthResult> results = []

        when:
        healthIndicator.result.subscribe(new TestSubscriber<HealthResult>({ HealthResult t -> results << t }))

        then:
        new PollingConditions().eventually {
            results.status == [expectedStatus]
        }

        and:
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

    private class TestSubscriber<T> implements Subscriber<T> {

        private final Consumer<T> onNext;

        private TestSubscriber(Consumer<T> onNext) {
            this.onNext = onNext
        }

        @Override
        void onSubscribe(Subscription s) {
            s.request(1)
        }

        @Override
        void onNext(T t) {
            this.onNext.accept(t)
        }

        @Override
        void onError(Throwable t) {
            throw t
        }

        @Override
        void onComplete() {
        }
    }
}
