package io.micronaut.microstream.metrics

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.exceptions.NoSuchBeanException
import io.micronaut.core.util.StringUtils
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest
@Property(name = "spec.name", value = "MicrostreamMetricsBinderSpec")
class MicroStreamMetricsBinderDisabledSpec extends Specification implements TestPropertyProvider {

    @TempDir
    @Shared
    File tempDir

    @Inject
    BeanContext beanContext

    @Inject
    @Client("/")
    HttpClient httpClient

    @Override
    Map<String, String> getProperties() {
        [
                "micronaut.metrics.binders.microstream.enabled": StringUtils.FALSE,
                "microstream.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                "microstream.storage.towns.storage-directory" : new File(tempDir, "towns").absolutePath,
        ]
    }

    void "metrics are not added"() {
        when:
        beanContext.getBean(MicroStreamMetricsBinder)

        then:
        thrown(NoSuchBeanException)

        when:
        Map<String, Object> response = httpClient.toBlocking().retrieve("/metrics", Map)

        then:
        !['microstream.people.fileCount',
          'microstream.people.liveDataLength',
          'microstream.people.totalDataLength',
          'microstream.towns.fileCount',
          'microstream.towns.liveDataLength',
          'microstream.towns.totalDataLength'
        ].any { response.names.contains(it) }
    }
}
