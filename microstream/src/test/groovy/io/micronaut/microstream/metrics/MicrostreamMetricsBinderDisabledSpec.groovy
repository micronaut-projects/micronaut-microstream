package io.micronaut.microstream.metrics

import groovy.json.JsonSlurper
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
class MicrostreamMetricsBinderDisabledSpec extends Specification implements TestPropertyProvider {

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
                "micronaut.metrics.export.atlas.enabled": StringUtils.FALSE,
                "microstream.storage.people.storage-directory-in-user-home": new File(tempDir, "people").absolutePath,
                "microstream.storage.towns.storage-directory-in-user-home" : new File(tempDir, "towns").absolutePath,
        ]
    }

    def "metrics are not added"() {
        when:
        beanContext.getBean(MicrostreamMetricsBinder)

        then:
        thrown(NoSuchBeanException)

        when:
        def response = new JsonSlurper().parseText(httpClient.toBlocking().retrieve("/metrics"))

        then:
        ![ 'microstream.people.fileCount',
          'microstream.people.liveDataLength',
          'microstream.people.totalDataLength',
          'microstream.towns.fileCount',
          'microstream.towns.liveDataLength',
          'microstream.towns.totalDataLength'
        ].any { response.names.contains(it) }
    }
}
