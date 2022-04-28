package io.micronaut.microstream.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import jakarta.inject.Singleton;
import one.microstream.storage.restadapter.types.ViewerObjectDescription;
import one.microstream.storage.restadapter.types.ViewerStorageFileStatistics;

import java.util.Date;

@Singleton
public class ObjectMapperBeanCreatedEventListener implements BeanCreatedEventListener<ObjectMapper> {

    @Override
    public ObjectMapper onCreated(BeanCreatedEvent<ObjectMapper> event) {
        return event.getBean()
            .addMixIn(ViewerStorageFileStatistics.class, ViewerStorageFileStatisticsMixin.class)
            .addMixIn(ViewerObjectDescription.class, ViewerObjectDescriptionMixin.class);
    }

    @JsonInclude
    private interface ViewerStorageFileStatisticsMixin {

        @SuppressWarnings("unused")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Date getCreationTime();
    }

    @JsonInclude
    private interface ViewerObjectDescriptionMixin {
    }
}
