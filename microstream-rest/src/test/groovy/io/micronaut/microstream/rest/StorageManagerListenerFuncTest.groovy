package io.micronaut.microstream.rest

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.micronaut.context.ApplicationContext
import io.micronaut.inject.qualifiers.Qualifiers
import one.microstream.storage.types.StorageManager
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

class StorageManagerListenerFuncTest extends Specification {

    @TempDir
    @Shared
    File tempDir

    def "listener detects storage being created"() {
        given:
        def applicationContext = ApplicationContext.run(
                'logger.levels.io.micronaut.microstream.rest': 'DEBUG',
                'microstream.rest.enabled': 'true',
                "microstream.storage.one.root-class": 'java.util.ArrayList',
                "microstream.storage.one.storage-directory": new File(tempDir, "one").absolutePath,
                "microstream.storage.two.root-class": 'java.util.ArrayList',
                "microstream.storage.two.storage-directory": new File(tempDir, "two").absolutePath,
        )
        def appender = attachListAppenderLogger(StorageManagerListener)

        when:
        def oneStore = applicationContext.getBean(StorageManager, Qualifiers.byName('one'))

        then:
        oneStore
        with(appender.list) {
            size() == 1
            get(0).formattedMessage.startsWith("StorageManager qualified as @Named('one') created: ")
        }

        when:
        def twoStore = applicationContext.getBean(StorageManager, Qualifiers.byName('two'))

        then:
        twoStore
        with(appender.list) {
            size() == 2
            get(1).formattedMessage.startsWith("StorageManager qualified as @Named('two') created: ")
        }

        cleanup:
        applicationContext.close()
    }

    private static ListAppender<ILoggingEvent> attachListAppenderLogger(Class<?> location) {
        def appender = new ListAppender<ILoggingEvent>()
        appender.start()
        ((Logger) LoggerFactory.getLogger(location)).addAppender(appender)
        appender
    }
}
