package io.micronaut.microstream.metrics

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import one.microstream.concurrency.XThreads
import one.microstream.storage.types.StorageManager
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest
@Property(name = "spec.name", value = "MicroStreamMetricsBinderSpec")
class MicroStreamMetricsBinderSpec extends Specification implements TestPropertyProvider {

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
                "microstream.storage.people.root-class": People.class.name,
                "microstream.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                "microstream.storage.towns.root-class": Towns.class.name,
                "microstream.storage.towns.storage-directory" : new File(tempDir, "towns").absolutePath,
        ]
    }


    void "metrics are added"() {
        when:
        Map<String, Object> response = httpClient.toBlocking().retrieve("/metrics", Map)

        then:
        response.names.containsAll([
                'microstream.people.globalFileCount', 'microstream.people.liveDataLength', 'microstream.people.totalDataLength',
                'microstream.towns.globalFileCount', 'microstream.towns.liveDataLength', 'microstream.towns.totalDataLength'
        ])

        when:
        SpecController controller = beanContext.getBean(SpecController)

        and:
        BigDecimal townDataLengthBefore = mostRecentMetric("microstream.towns.liveDataLength")
        controller.storeTown("This is a really long name for a town, I probably made it up")
        BigDecimal townDataLengthAfter = mostRecentMetric("microstream.towns.liveDataLength")

        then:
        townDataLengthBefore < townDataLengthAfter

        when:
        BigDecimal peopleDataLengthBefore = mostRecentMetric("microstream.people.liveDataLength")
        controller.storePerson("Tim")
        BigDecimal peopleDataLengthAfter = mostRecentMetric("microstream.people.liveDataLength")

        then:
        peopleDataLengthBefore < peopleDataLengthAfter

        and: "The town datastore is bigger than the people one"
        townDataLengthAfter > peopleDataLengthAfter
    }

    private BigDecimal mostRecentMetric(String metricName) {
        httpClient.toBlocking().retrieve("/metrics/$metricName", Map).measurements*.value.head() as BigDecimal
    }

    @Singleton
    @Requires(property = "spec.name", value = "MicroStreamMetricsBinderSpec")
    static class SpecController {

        private StorageManager townManager
        private StorageManager peopleManager

        SpecController(
                @Named('towns') StorageManager townManager,
                @Named('people') StorageManager peopleManager
        ) {
            this.townManager = townManager
            this.peopleManager = peopleManager
        }

        def storeTown(String town) {
            XThreads.executeSynchronized { ->
                townManager.store(town)
                townManager.storeAll()
            }
        }

        def storePerson(String person) {
            XThreads.executeSynchronized { ->
                peopleManager.store(person)
                peopleManager.storeAll()
            }
        }
    }

    @Introspected
    static class Towns {
        List<String> towns = []
    }

    @Introspected
    static class People {
        List<String> people = []
    }
}
