package io.micronaut.microstream.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.BeanContext
import io.micronaut.core.beans.BeanIntrospection
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

    void "RootObject is annotated with Introspected"() {
        when:
        BeanIntrospection.getIntrospection(RootObject)

        then:
        noExceptionThrown()
    }

    void "RootObject::toString() does not throw NPE"() {
        when:
        new RootObject(null, null).toString()

        then:
        noExceptionThrown()

        when:
        new RootObject("tim", "1").toString()

        then:
        noExceptionThrown()
    }

    void "valid RootObject does not trigger any constraint exception"() {
        when:
        RootObject el = new RootObject("rooty", "1")

        then:
        validator.validate(el).isEmpty()
    }

    void "name is required"() {
        when:
        RootObject el = new RootObject(null, "1")

        then:
        !validator.validate(el).isEmpty()
    }

    void "objectId is required"() {
        when:
        RootObject el = new RootObject("rooty", null)

        then:
        !validator.validate(el).isEmpty()
    }

    void "values are present in json"() {
        given:
        RootObject el = new RootObject("rooty", "1")

        when:
        String json = objectMapper.writeValueAsString(el)

        then:
        json == '{"name":"rooty","objectId":"1"}'
    }

    void "round trip works as expected"() {
        given:
        RootObject el = new RootObject("rooty", "1")

        when:
        String json = objectMapper.writeValueAsString(el)

        and:
        RootObject object = objectMapper.readValue(json, RootObject)

        then:
        object.name == el.name
        object.objectId == el.objectId
    }
}
