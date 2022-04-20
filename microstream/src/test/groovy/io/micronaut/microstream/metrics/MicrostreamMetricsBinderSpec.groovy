package io.micronaut.microstream.metrics


import groovy.json.JsonSlurper
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.microstream.conf.RootInstanceProvider
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import one.microstream.concurrency.XThreads
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest
@Property(name = "spec.name", value = "MicrostreamMetricsBinderSpec")
class MicrostreamMetricsBinderSpec extends Specification implements TestPropertyProvider {

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
                "micronaut.metrics.export.atlas.enabled": StringUtils.FALSE,
                "microstream.storage.people.storage-directory-in-user-home": new File(tempDir, "people").absolutePath,
                "microstream.storage.towns.storage-directory-in-user-home" : new File(tempDir, "towns").absolutePath,
        ]
    }

    def "metrics are added"() {
        when:
        def response = new JsonSlurper().parseText(httpClient.toBlocking().retrieve("/metrics"))

        then:
        response.names.containsAll([
                'microstream.people.fileCount', 'microstream.people.liveDataLength', 'microstream.people.totalDataLength',
                'microstream.towns.fileCount', 'microstream.towns.liveDataLength', 'microstream.towns.totalDataLength'
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
        new JsonSlurper().parseText(httpClient.toBlocking().retrieve("/metrics/$metricName")).measurements*.value.head() as BigDecimal
    }

    @Singleton
    @Requires(property = "spec.name", value = "MicrostreamMetricsBinderSpec")
    static class SpecController {

        private EmbeddedStorageManager townManager
        private EmbeddedStorageManager peopleManager

        SpecController(
                @Named('towns') EmbeddedStorageManager townManager,
                @Named('people') EmbeddedStorageManager peopleManager
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

    @Named("towns")
    @Singleton
    @Requires(property = "spec.name", value = "MicrostreamMetricsBinderSpec")
    static class TownsRootInstanceProvider implements RootInstanceProvider<Towns> {

        @Override
        Towns rootInstance() {
            new Towns()
        }
    }

    static class Towns {
        List<String> towns = []
    }

    @Named("people")
    @Singleton
    @Requires(property = "spec.name", value = "MicrostreamMetricsBinderSpec")
    static class PeopleRootInstanceProvider implements RootInstanceProvider<People> {

        @Override
        People rootInstance() {
            new People();
        }
    }

    static class People {
        List<String> people = []
    }
}
