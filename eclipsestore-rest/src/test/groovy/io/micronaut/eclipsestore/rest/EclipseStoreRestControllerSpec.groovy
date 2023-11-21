package io.micronaut.eclipsestore.rest

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.annotation.NonNull
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.eclipse.store.storage.restadapter.types.ViewerObjectDescription
import org.eclipse.store.storage.restadapter.types.ViewerStorageFileStatistics
import org.eclipse.store.storage.types.StorageManager
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest
@Property(name = "spec.name", value = "EclipseStoreRestControllerSpec")
class EclipseStoreRestControllerSpec extends Specification implements TestPropertyProvider {

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
                'eclipsestore.rest.enabled': 'true',
                "eclipsestore.storage.people.root-class": People.class.name,
                "eclipsestore.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                "eclipsestore.storage.towns.root-class": Towns.class.name,
                "eclipsestore.storage.towns.storage-directory" : new File(tempDir, "towns").absolutePath,
        ]
    }

    void 'file statistics exists'() {
        when:
        def statistics = stats()

        then:
        statistics.creationTime
        !statistics.channelStatistics.empty

        when:
        statistics = stats('towns')

        then:
        statistics.creationTime
        !statistics.channelStatistics.empty
    }

    void 'dictionary exists'() {
        when:
        def dictionary = dictionary()

        then:
        dictionary.contains('primitive 8 bit integer signed')

        and: 'Contains JDK8 loadFactor addition'
        dictionary.contains('java.util.LinkedHashSet{\n\tfloat  loadFactor')

        and: 'Contains JDK17 addition'
        dictionary.contains('java.util.ImmutableCollections$Set12')
    }

    void 'Object traversal works as expected'() {
        given:
        def manager = beanContext.getBean(StorageManager, Qualifiers.byName("people"))
        def people = manager.root().people
        people << "Tim"
        people << "Sergio"
        manager.store(people)

        when:
        def root = root()

        then:
        root.name == 'ROOT'
        root.objectId != null

        when:
        def obj = object(root.objectId)

        then:
        obj.objectId == root.objectId
        obj.data.size() == 1

        when:
        String id = obj.data[0]
        obj = object(id)

        then:
        obj.objectId == id

        // With the Jdk8 persistence layer, the first item is a list capacity, and the second the list itself
        obj.data.size() == 2
        // Which contains 2 items
        obj.data[1].size() == 2

        when:
        String timId = obj.data[1][0]
        String sergioId = obj.data[1][1]

        def tim = object(timId)
        def sergio = object(sergioId)

        then:
        tim.objectId == timId
        tim.data.size() == 1
        tim.data[0] == "Tim"

        and:
        sergio.objectId == sergioId
        sergio.data.size() == 1
        sergio.data[0] == "Sergio"
    }

    @NonNull
    private ViewerStorageFileStatistics stats(String manager = 'people') {
        read("/eclipsestore/$manager/maintenance/filesStatistics", ViewerStorageFileStatistics)
    }

    @NonNull
    private RootObject root(String manager = 'people') {
        read("/eclipsestore/$manager/root", RootObject)
    }

    @NonNull
    private String dictionary(String manager = 'people') {
        httpClient.toBlocking().retrieve("/eclipsestore/$manager/dictionary")
    }

    @NonNull
    private ViewerObjectDescription object(String id, String manager = 'people') {
        return read("/eclipsestore/$manager/object/$id", ViewerObjectDescription)
    }

    @NonNull
    private <T> T read(String path, Class<T> clazz) {
        httpClient.toBlocking().retrieve(path, clazz)
    }
}
