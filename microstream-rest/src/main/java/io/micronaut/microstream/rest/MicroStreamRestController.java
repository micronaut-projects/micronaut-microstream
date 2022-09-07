/*
 * Copyright 2017-2022 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.microstream.rest;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.microstream.conf.EmbeddedStorageConfigurationProvider;
import io.micronaut.serde.annotation.SerdeImport;
import one.microstream.storage.restadapter.types.StorageRestAdapter;
import one.microstream.storage.restadapter.types.ViewerChannelStatistics;
import one.microstream.storage.restadapter.types.ViewerFileStatistics;
import one.microstream.storage.restadapter.types.ViewerObjectDescription;
import one.microstream.storage.restadapter.types.ViewerStorageFileStatistics;
import one.microstream.storage.types.StorageManager;

/**
 * MicroStream REST controller for a single StorageManager.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Requires(bean = StorageManager.class)
@Requires(bean = EmbeddedStorageConfigurationProvider.class)
@Controller("/${" + MicroStreamRestControllerConfigurationProperties.PREFIX + ".path:" + MicroStreamRestControllerConfigurationProperties.DEFAULT_PATH + "}")
@SerdeImport(value = ViewerStorageFileStatistics.class, mixin = ViewerStorageFileStatisticsMixin.class)
@SerdeImport(value = ViewerObjectDescription.class, mixin = ViewerObjectDescriptionMixin.class)
@SerdeImport(value = ViewerChannelStatistics.class)
@SerdeImport(value = ViewerFileStatistics.class)
public class MicroStreamRestController {

    private static final long DEFAULT_FIXED_OFFSET = 0L;
    private static final long DEFAULT_FIXED_LENGTH = Long.MAX_VALUE;
    private static final long DEFAULT_VARIABLE_OFFSET = 0L;
    private static final long DEFAULT_VARIABLE_LENGTH = Long.MAX_VALUE;

    private final MicroStreamRestService service;

    /**
     * A default controller to expose the expected MicroStream REST API per storage manager.
     *
     * @param service the service to query MicroStream via the relevant {@link StorageRestAdapter}
     */
    public MicroStreamRestController(MicroStreamRestService service) {
        this.service = service;
    }

    /**
     * Get the user defined root.
     *
     * @return the root object
     */
    @Get("/root")
    public RootObject getRoot() {
        return service.singleAdapter().map(this::getRoot).orElse(null);
    }

    /**
     * Get the user defined root.
     *
     * @param name the name of the storage adapter
     * @return the root object
     */
    @Get("/{name}/root")
    public RootObject getRoot(@PathVariable String name) {
        return getRoot(service.getAdapter(name));
    }

    private RootObject getRoot(StorageRestAdapter adapter) {
        return new RootObject(adapter.getUserRoot());
    }

    /**
     * Get storage type dictionary.
     *
     * @return storage type dictionary as a single string
     */
    @Get("/dictionary")
    public String getDictionary() {
        return service.singleAdapter().map(this::getDictionary).orElse(null);
    }

    /**
     * Get storage type dictionary.
     *
     * @param name the name of the storage adapter
     * @return storage type dictionary as a single string
     */
    @Get("/{name}/dictionary")
    public String getDictionary(@PathVariable String name) {
        return getDictionary(service.getAdapter(name));
    }

    @NonNull
    private String getDictionary(@NonNull StorageRestAdapter adapter) {
        return adapter.getTypeDictionary();
    }

    /**
     * Get an object by its id.
     *
     * @param oid            object id of the requested object
     * @param valueLength    limit size of returned value elements to this value
     * @param fixedOffset    index of the first fix sized element to fetch
     * @param fixedLength    number of fix sized elements to be fetched
     * @param variableOffset index of the first element in variable sized collections to be fetched
     * @param variableLength number of elements to be fetched from variable sized collections
     * @param references     resolve top level references and return them with this request
     * @return the object
     */
    @Get("/object/{oid}")
    @SuppressWarnings("java:S107") // This has more than 7 arguments
    public ViewerObjectDescription getObject(
        @PathVariable String oid,
        @Nullable @QueryValue Long valueLength,
        @Nullable @QueryValue Long fixedOffset,
        @Nullable @QueryValue Long fixedLength,
        @Nullable @QueryValue Long variableOffset,
        @Nullable @QueryValue Long variableLength,
        @Nullable @QueryValue Boolean references
    ) {
        return service.singleAdapter()
            .map(a -> getObject(a, oid, valueLength, fixedOffset, fixedLength, variableOffset, variableLength, references))
            .orElse(null);
    }

    /**
     * Get an object by its id.
     *
     * @param name the name of the storage adapter
     * @param oid            object id of the requested object
     * @param valueLength    limit size of returned value elements to this value
     * @param fixedOffset    index of the first fix sized element to fetch
     * @param fixedLength    number of fix sized elements to be fetched
     * @param variableOffset index of the first element in variable sized collections to be fetched
     * @param variableLength number of elements to be fetched from variable sized collections
     * @param references     resolve top level references and return them with this request
     * @return the object
     */
    @Get("/{name}/object/{oid}")
    @SuppressWarnings("java:S107") // This has more than 7 arguments
    public ViewerObjectDescription getObject(
        @PathVariable String name,
        @PathVariable String oid,
        @Nullable @QueryValue Long valueLength,
        @Nullable @QueryValue Long fixedOffset,
        @Nullable @QueryValue Long fixedLength,
        @Nullable @QueryValue Long variableOffset,
        @Nullable @QueryValue Long variableLength,
        @Nullable @QueryValue Boolean references
    ) {
        return getObject(service.getAdapter(name), oid, valueLength, fixedOffset, fixedLength, variableOffset, variableLength, references);
    }

    @SuppressWarnings("java:S107") // This has more than 7 arguments
    @NonNull
    private ViewerObjectDescription getObject(
        @NonNull StorageRestAdapter adapter,
        @NonNull String oid,
        @Nullable Long valueLength,
        @Nullable Long fixedOffset,
        @Nullable Long fixedLength,
        @Nullable Long variableOffset,
        @Nullable Long variableLength,
        @Nullable Boolean references
    ) {
        return adapter.getObject(
            Long.parseLong(oid),
            fixedOffset != null ? fixedOffset : DEFAULT_FIXED_OFFSET,
            fixedLength != null ? fixedLength : DEFAULT_FIXED_LENGTH,
            variableOffset != null ? variableOffset : DEFAULT_VARIABLE_OFFSET,
            variableLength != null ? variableLength : DEFAULT_VARIABLE_LENGTH,
            valueLength != null ? valueLength : adapter.getDefaultValueLength(),
            references != null && references
        );
    }

    /**
     * Get statistics for all storage files.
     *
     * @return the statistics as json
     */
    @Get("/maintenance/filesStatistics")
    public ViewerStorageFileStatistics getFilesStatistics() {
        return service.singleAdapter()
            .map(this::getFilesStatistics)
            .orElse(null);
    }

    /**
     * Get statistics for all storage files.
     *
     * @param name the name of the storage adapter
     * @return the statistics as json
     */
    @Get("/{name}/maintenance/filesStatistics")
    public ViewerStorageFileStatistics getFilesStatistics(@PathVariable String name) {
        return getFilesStatistics(service.getAdapter(name));
    }

    @NonNull
    private ViewerStorageFileStatistics getFilesStatistics(@NonNull StorageRestAdapter adapter) {
        return adapter.getStorageFilesStatistics();
    }
}
