package io.micronaut.eclipsestore.rest

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.core.beans.BeanIntrospection
import io.micronaut.serde.ObjectMapper
import spock.lang.Specification

import jakarta.validation.Validator

class RootObjectSpec extends Specification {

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
        given:
        def ctx = ApplicationContext.run(Environment.TEST)
        def validator = ctx.getBean(Validator)

        when:
        RootObject el = new RootObject("rooty", "1")

        then:
        validator.validate(el).isEmpty()

        cleanup:
        ctx.close()
    }

    void "name is required"() {
        given:
        def ctx = ApplicationContext.run(Environment.TEST)
        def validator = ctx.getBean(Validator)

        when:
        RootObject el = new RootObject(null, "1")

        then:
        !validator.validate(el).isEmpty()

        cleanup:
        ctx.close()
    }

    void "objectId is required"() {
        given:
        def ctx = ApplicationContext.run(Environment.TEST)
        def validator = ctx.getBean(Validator)

        when:
        RootObject el = new RootObject("rooty", null)

        then:
        !validator.validate(el).isEmpty()

        cleanup:
        ctx.close()
    }

    void "values are present in json"() {
        given:
        def ctx = ApplicationContext.run(Environment.TEST)
        def objectMapper = ctx.getBean(ObjectMapper)

        and:
        RootObject el = new RootObject("rooty", "1")

        when:
        String json = objectMapper.writeValueAsString(el)

        then:
        json == '{"name":"rooty","objectId":"1"}'

        cleanup:
        ctx.close()
    }

    void "round trip works as expected"() {
        given:
        def ctx = ApplicationContext.run(Environment.TEST)
        def objectMapper = ctx.getBean(ObjectMapper)

        and:
        RootObject el = new RootObject("rooty", "1")

        when:
        String json = objectMapper.writeValueAsString(el)

        and:
        RootObject object = objectMapper.readValue(json, RootObject)

        then:
        object.name == el.name
        object.objectId == el.objectId

        cleanup:
        ctx.close()
    }
}
