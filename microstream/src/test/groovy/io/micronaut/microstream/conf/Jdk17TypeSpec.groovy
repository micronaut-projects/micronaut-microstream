package io.micronaut.microstream.conf

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.core.annotation.Introspected
import io.micronaut.runtime.server.EmbeddedServer
import one.microstream.storage.types.StorageManager
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

class Jdk17TypeSpec extends Specification {

    @TempDir
    @Shared
    File tempDir

    void "jdk17 types are enabled by default"() {
        given:
        EmbeddedServer ctx = ApplicationContext.run(EmbeddedServer, [
                "microstream.storage.default.root-class": BlueFlowers.class.name,
                "microstream.storage.default.storage-directory": new File(tempDir, "orange").absolutePath,
                ], Environment.TEST)
        def manager = ctx.applicationContext.getBean(StorageManager)

        expect:
        manager.typeDictionary().lookupTypeByName('java.util.ImmutableCollections$Set12') != null

        cleanup:
        ctx.stop()
    }

    void "jdk17 types can be disabled"() {
        given:
        EmbeddedServer ctx = ApplicationContext.run(EmbeddedServer, [
                "microstream.storage.default.root-class": BlueFlowers.class.name,
                "microstream.storage.default.enable-jdk17-types": 'false',
                "microstream.storage.default.storage-directory": new File(tempDir, "orange").absolutePath,
        ], Environment.TEST)
        def manager = ctx.applicationContext.getBean(StorageManager)

        when:
        manager.typeDictionary().lookupTypeByName('java.util.ImmutableCollections$Set12') == null

        then:
        noExceptionThrown()

        cleanup:
        ctx.stop()
    }

    @Introspected
    static class BlueFlowers {
        List<String> flowers = []
    }
}
