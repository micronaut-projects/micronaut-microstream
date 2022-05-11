package example.micronaut

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.StringUtils
import io.micronaut.microstream.cache.CacheConfiguration
import spock.lang.Specification

class MicrostreamCacheDisabledSpec extends Specification {

    void "you can disable cache by setting microstream.cache.enabled to false"() {
        given:
        ApplicationContext context = ApplicationContext.run(
                ['microstream.cache.enabled': StringUtils.FALSE])

        expect:
        !context.containsBean(CacheConfiguration.class)

        cleanup:
        context.close()
    }
}
