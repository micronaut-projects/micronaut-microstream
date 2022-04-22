package io.micronaut.microstream.health

import io.micronaut.context.ApplicationContext
import spock.lang.Specification

class MicrostreamHealthIndicatorSpec extends Specification {

    void "test the health indicator can be disabled"() {
        given:
        ApplicationContext context = ApplicationContext.run(['endpoints.health.microstream.enabled': false])

        expect:
        !context.containsBean(MicrostreamHealthIndicator)

        cleanup:
        context.close()
    }
}
