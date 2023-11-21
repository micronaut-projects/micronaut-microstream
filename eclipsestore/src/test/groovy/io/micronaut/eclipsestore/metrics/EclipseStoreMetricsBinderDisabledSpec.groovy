package io.micronaut.eclipsestore.metrics

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
@Property(name = "spec.name", value = "EclipseStoreMetricsBinderSpec")
class EclipseStoreMetricsBinderDisabledSpec extends Specification implements TestPropertyProvider {

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
                "micronaut.metrics.binders.eclipsestore.enabled": StringUtils.FALSE,
                "eclipsestore.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                "eclipsestore.storage.towns.storage-directory" : new File(tempDir, "towns").absolutePath,
        ]
    }

    void "metrics are not added"() {
        when:
        beanContext.getBean(EclipseStoreMetricsBinder)

        then:
        thrown(NoSuchBeanException)

        when:
        Map<String, Object> response = httpClient.toBlocking().retrieve("/metrics", Map)

        then:
        !['eclipsestore.people.fileCount',
          'eclipsestore.people.liveDataLength',
          'eclipsestore.people.totalDataLength',
          'eclipsestore.towns.fileCount',
          'eclipsestore.towns.liveDataLength',
          'eclipsestore.towns.totalDataLength'
        ].any { response.names.contains(it) }
    }
}
