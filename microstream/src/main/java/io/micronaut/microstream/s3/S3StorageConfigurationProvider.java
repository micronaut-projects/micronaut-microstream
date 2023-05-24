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
package io.micronaut.microstream.s3;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.microstream.conf.RootClassConfigurationProvider;

import java.util.Optional;

/**
 * @author Tim Yates
 * @since 2.0.0
 */
public interface S3StorageConfigurationProvider extends RootClassConfigurationProvider {

    /**
     * The name qualifier of the defined S3Client to use.  If unset, a client with the same name as the storage will be used.
     *
     * @return Returns the name of the S3Client to use.
     */
    @NonNull
    Optional<String> getS3ClientName();

    /**
     *
     * @return Returns the name of the bucket to use.
     */
    @NonNull
    String getBucketName();
}
