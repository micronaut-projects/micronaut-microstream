package io.micronaut.eclipsestore.metrics

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
import org.eclipse.serializer.concurrency.XThreads
import org.eclipse.store.storage.types.StorageManager
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest
@Property(name = "spec.name", value = "EclipseStoreMetricsBinderSpec")
class EclipseStoreMetricsBinderSpec extends Specification implements TestPropertyProvider {

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
                "eclipsestore.storage.people.root-class": People.class.name,
                "eclipsestore.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                "eclipsestore.storage.towns.root-class": Towns.class.name,
                "eclipsestore.storage.towns.storage-directory" : new File(tempDir, "towns").absolutePath,
        ]
    }


    void "metrics are added"() {
        when:
        Map<String, Object> response = httpClient.toBlocking().retrieve("/metrics", Map)

        then:
        response.names.containsAll([
                'eclipsestore.people.globalFileCount', 'eclipsestore.people.liveDataLength', 'eclipsestore.people.totalDataLength',
                'eclipsestore.towns.globalFileCount', 'eclipsestore.towns.liveDataLength', 'eclipsestore.towns.totalDataLength'
        ])

        when:
        SpecController controller = beanContext.getBean(SpecController)

        and:
        BigDecimal townDataLengthBefore = mostRecentMetric("eclipsestore.towns.liveDataLength")
        controller.storeTown("This is a really long name for a town, I probably made it up")
        BigDecimal townDataLengthAfter = mostRecentMetric("eclipsestore.towns.liveDataLength")

        then:
        townDataLengthBefore < townDataLengthAfter

        when:
        BigDecimal peopleDataLengthBefore = mostRecentMetric("eclipsestore.people.liveDataLength")
        controller.storePerson("Tim")
        BigDecimal peopleDataLengthAfter = mostRecentMetric("eclipsestore.people.liveDataLength")

        then:
        peopleDataLengthBefore < peopleDataLengthAfter

        and: "The town datastore is bigger than the people one"
        townDataLengthAfter > peopleDataLengthAfter
    }

    private BigDecimal mostRecentMetric(String metricName) {
        httpClient.toBlocking().retrieve("/metrics/$metricName", Map).measurements*.value.head() as BigDecimal
    }

    @Singleton
    @Requires(property = "spec.name", value = "EclipseStoreMetricsBinderSpec")
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
