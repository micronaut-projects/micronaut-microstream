package io.micronaut.microstream.rest

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.core.annotation.NonNull
import org.slf4j.LoggerFactory
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.TempDir

class MicroStreamRestStartupWarningSpec extends Specification {

    @TempDir
    File tempDir

    @AutoCleanup("stop")
    ListAppender<ILoggingEvent> listAppender

    void setup() {
        listAppender = new ListAppender<>()
        listAppender.start()
        LoggerFactory.getLogger(MicroStreamRestStartupWarning).addAppender(listAppender)
    }

    void "logging is shown when enabled"() {
        given:
        def server = startServer(
                "microstream.storage.people.root-class": People.class.name,
                "microstream.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                'microstream.rest.enabled': 'true',
                "not-test"
        )

        expect:
        with(listAppender.list[0]) {
            level == Level.WARN
            formattedMessage == MicroStreamRestStartupWarning.WARNING_MESSAGE
        }

        cleanup:
        server.stop()
    }

    void "logging is not shown under test"() {
        given:
        def server = startServer(
                "microstream.storage.people.root-class": People.class.name,
                "microstream.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                'microstream.rest.enabled': 'true',
        )

        expect:
        listAppender.list.empty

        cleanup:
        server.stop()
    }

    void "logging is not shown when disabled"() {
        given:
        def server = startServer(
                "microstream.storage.people.root-class": People.class.name,
                "microstream.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                'not-test'
        )

        expect:
        listAppender.list.empty

        cleanup:
        server.stop()
    }

    @NonNull
    ApplicationContext startServer(Map props = [:], String env = Environment.TEST) {
        ApplicationContext.builder().deduceEnvironment(false).properties(props).environments(env).build().start()
    }
}
