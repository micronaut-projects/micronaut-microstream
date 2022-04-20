package io.micronaut.microstream.docs;

import io.micronaut.context.BeanContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.microstream.conf.EmbeddedStorageConfigurationProvider;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = "multiple")
class MultipleEmbeddedStorageTest {

    @Inject
    BeanContext beanContext;

    @Test
    void thereAreMultiple() {
        assertEquals(2, beanContext.getBeansOfType(EmbeddedStorageConfigurationProvider.class).size());
        assertTrue(beanContext.containsBean(EmbeddedStorageConfigurationProvider.class, Qualifiers.byName("one-microstream-instance")));
        assertTrue(beanContext.containsBean(EmbeddedStorageConfigurationProvider.class, Qualifiers.byName("another-microstream-instance")));
    }
}
