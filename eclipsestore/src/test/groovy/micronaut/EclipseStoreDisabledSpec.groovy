package micronaut

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.StringUtils
import io.micronaut.eclipsestore.conf.EclipseStoreConfiguration
import spock.lang.Specification

class EclipseStoreDisabledSpec extends Specification {

    void "you can disable the module by setting eclipsestore.enabled to false"() {
        given:
        ApplicationContext context = ApplicationContext.run(
                ['eclipsestore.enabled': StringUtils.FALSE])

        expect:
        !context.containsBean(EclipseStoreConfiguration.class)

        cleanup:
        context.close()
    }
}
