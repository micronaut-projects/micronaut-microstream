package io.micronaut.eclipsestore.health

import io.micronaut.context.ApplicationContext
import spock.lang.Specification

class EclipseStoreHealthIndicatorSpec extends Specification {

    void "test the health indicator can be disabled"() {
        given:
        ApplicationContext context = ApplicationContext.run(['endpoints.health.eclipsestore.enabled': false])

        expect:
        !context.containsBean(EclipseStoreHealthIndicator)

        cleanup:
        context.close()
    }
}
