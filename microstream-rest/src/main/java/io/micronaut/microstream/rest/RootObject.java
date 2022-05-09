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

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import one.microstream.storage.restadapter.types.ViewerRootDescription;

import javax.validation.constraints.NotBlank;

/**
 * This object represents a root object for the Microstream REST api.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Introspected
public class RootObject {

    /**
     * root name.
     */
    @NonNull
    @NotBlank
    private final String name;

    /**
     * the root object id.
     */
    @NonNull
    @NotBlank
    private final String objectId;

    /**
     * Construct a root object from a name and id.
     *
     * @param name     the name of the root object
     * @param objectId the id of the root object
     */
    public RootObject(@NonNull String name, @NonNull String objectId) {
        this.name = name;
        this.objectId = objectId;
    }

    /**
     * Construct a root object from the MicroStream description.
     *
     * @param userRoot the root description from MicroStream
     */
    public RootObject(@NonNull ViewerRootDescription userRoot) {
        this.name = userRoot.getName();
        this.objectId = Long.toString(userRoot.getObjectId());
    }

    /**
     * @return the name of the root object
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * @return the id of the root object
     */
    @NonNull
    public String getObjectId() {
        return objectId;
    }
}
