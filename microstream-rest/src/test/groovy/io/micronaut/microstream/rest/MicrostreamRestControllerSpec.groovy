package io.micronaut.microstream.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.annotation.NonNull
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import one.microstream.storage.restadapter.types.ViewerObjectDescription
import one.microstream.storage.restadapter.types.ViewerStorageFileStatistics
import one.microstream.storage.types.StorageManager
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest
@Property(name = "spec.name", value = "MicrostreamRestControllerSpec")
class MicrostreamRestControllerSpec extends Specification implements TestPropertyProvider {

    @TempDir
    @Shared
    File tempDir

    @Inject
    BeanContext beanContext

    @Inject
    @Client("/")
    HttpClient httpClient

    @Inject
    ObjectMapper objectMapper

    @Override
    Map<String, String> getProperties() {
        [
                "microstream.storage.people.root-class": People.class.name,
                "microstream.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                "microstream.storage.towns.root-class": Towns.class.name,
                "microstream.storage.towns.storage-directory" : new File(tempDir, "towns").absolutePath,
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
        obj.data.size() == 1
        // Which contains 2 items
        obj.data[0].size() == 2

        when:
        String timId = obj.data[0][0]
        String sergioId = obj.data[0][1]

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
        read("/microstream/$manager/maintenance/filesStatistics", ViewerStorageFileStatistics)
    }

    @NonNull
    private RootObject root(String manager = 'people') {
        read("/microstream/$manager/root", RootObject)
    }

    @NonNull
    private ViewerObjectDescription object(String id, String manager = 'people') {
        return read("/microstream/$manager/object/$id", ViewerObjectDescription)
    }

    @NonNull
    private <T> T read(String path, Class<T> clazz) {
        objectMapper.readValue(httpClient.toBlocking().retrieve(path), clazz)
    }
}
