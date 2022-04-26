package io.micronaut.microstream.annotations

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.inject.visitor.VisitorContext
import spock.lang.Specification

class StoreRootAnnotationMapperSpec extends Specification {

    void "StoreRoot is mapped to Store"() {
        when:
        StoreRootAnnotationMapper mapper = new StoreRootAnnotationMapper()
        def annotation = Stub(AnnotationValue<StoreRoot>) {
            enumValue(_, StoringStrategy.class) >> Optional.of(StoringStrategy.EAGER)
            get(_, String.class) >> Optional.of("main")
        }
        def visitorContext = Mock(VisitorContext)
        List<AnnotationValue<?>> result = mapper.map(annotation, visitorContext)

        then:
        result
        result.size() == 1

        when:
        AnnotationValue<Store> annotationValue = (AnnotationValue<Store>) result[0]

        then:
        annotationValue.stringValue("name").isPresent()
        "main" == annotationValue.stringValue("name").get()
        annotationValue.booleanValue("root").orElse(false)
        StoringStrategy.EAGER == annotationValue.enumValue("strategy", StoringStrategy.class).orElse(StoringStrategy.LAZY)
        !annotationValue.stringValues("params")
        !annotationValue.booleanValue("result").orElse(false)

        and:
        StoreRoot.class == mapper.annotationType()

    }
}
