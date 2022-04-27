package io.micronaut.microstream.rest

import io.micronaut.context.BeanContext
import io.micronaut.core.beans.BeanIntrospection
import io.micronaut.core.type.Argument
import io.micronaut.serde.ObjectMapper
import io.micronaut.serde.SerdeIntrospections
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

import javax.validation.Validator

@MicronautTest(startApplication = false)
class RootObjectSpec extends Specification {
    @Inject
    ObjectMapper objectMapper

    @Inject
    Validator validator

    @Inject
    BeanContext beanContext

    void "RootObject is annotated with @Serdeable.Deserializable"() {
        given:
        SerdeIntrospections serdeIntrospections = beanContext.getBean(SerdeIntrospections)

        when:
        serdeIntrospections.getDeserializableIntrospection(Argument.of(RootObject))

        then:
        noExceptionThrown()
    }

    void "RootObject is annotated with @Serdeable.Serializable"() {
        given:
        SerdeIntrospections serdeIntrospections = beanContext.getBean(SerdeIntrospections)

        when:
        serdeIntrospections.getSerializableIntrospection(Argument.of(RootObject))

        then:
        noExceptionThrown()
    }

    void "RootObject is annotated with Introspected"() {
        when:
        BeanIntrospection.getIntrospection(RootObject)

        then:
        noExceptionThrown()
    }

    void "RootObject::toString() does not throw NPE"() {
        when:
        new RootObject().toString()

        then:
        noExceptionThrown()
    }

    void "valid RootObject does not trigger any constraint exception"() {
        when:
        RootObject el = validRootObject()

        then:
        validator.validate(el).isEmpty()
    }

    void "name is required"() {
        given:
        RootObject el = validRootObject()

        when:
        el.name = null

        then:
        !validator.validate(el).isEmpty()
    }

    void "objectId is required"() {
        given:
        RootObject el = validRootObject()

        when:
        el.objectId = null

        then:
        !validator.validate(el).isEmpty()
    }

    void "values are present in json"() {
        given:
        RootObject el = validRootObject()

        when:
        String json = objectMapper.writeValueAsString(el)

        then:
        json == '{"name":"rooty","objectId":"1"}'
    }

    void "round trip works as expected"() {
        given:
        RootObject el = validRootObject()

        when:
        String json = objectMapper.writeValueAsString(el)

        and:
        RootObject object = objectMapper.readValue(json, RootObject)

        then:
        object.name == el.name
        object.objectId == el.objectId
    }

    static RootObject validRootObject() {
        RootObject el = new RootObject()
        el.name = "rooty"
        el.objectId = 1
        el
    }
}
