package io.micronaut.eclipsestore.annotations

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.inject.visitor.VisitorContext
import spock.lang.Specification

class StoreParamsAnnotationMapperSpec extends Specification {

    void "StoreParams is mapped to Store"() {
        when:
        StoreParamsAnnotationMapper mapper = new StoreParamsAnnotationMapper()
        def annotation = Stub(AnnotationValue<StoreParams>) {
            enumValue(_, StoringStrategy.class) >> Optional.of(StoringStrategy.EAGER)
            get(_, String.class) >> Optional.of("main")
            stringValues() >> ['customers']
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
        !annotationValue.booleanValue("root").orElse(false)
        StoringStrategy.EAGER == annotationValue.enumValue("strategy", StoringStrategy.class).orElse(StoringStrategy.LAZY)
        ["customers"] == annotationValue.stringValues("parameters")
        !annotationValue.booleanValue("result").orElse(false)

        and:
        StoreParams.class == mapper.annotationType()
    }
}
