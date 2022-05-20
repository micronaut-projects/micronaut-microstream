package micronaut

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.StringUtils
import io.micronaut.microstream.conf.MicroStreamConfiguration
import spock.lang.Specification

class MicroStreamDisabledSpec extends Specification {

    void "you can disable the module by setting microstream.enabled to false"() {
        given:
        ApplicationContext context = ApplicationContext.run(
                ['microstream.enabled': StringUtils.FALSE])

        expect:
        !context.containsBean(MicroStreamConfiguration.class)

        cleanup:
        context.close()
    }
}
