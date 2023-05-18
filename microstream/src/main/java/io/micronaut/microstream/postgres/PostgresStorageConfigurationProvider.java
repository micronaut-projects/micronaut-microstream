/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.microstream.postgres;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.naming.Named;

/**
 * @author Tim Yates
 * @since 1.0.0
 */
public interface PostgresStorageConfigurationProvider extends Named {
    /**
     * Returns the class of the Root Instance.
     * <a href="https://docs.microstream.one/manual/storage/root-instances.html">Root Instances</a>
     * @return Class for the Root Instance.
     */
    @Nullable
    Class<?> getRootClass();

    /**
     *
     * @return Returns the name of the table to use.
     */
    String getTableName();
}
