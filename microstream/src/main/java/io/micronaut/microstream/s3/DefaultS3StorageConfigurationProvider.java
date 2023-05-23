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

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.Optional;

/**
 * @author Tim Yates
 * @since 2.0.0
 */
@EachProperty("microstream.s3.storage")
public class DefaultS3StorageConfigurationProvider implements S3StorageConfigurationProvider {

    @NonNull
    private Class<?> rootClass;

    private final String name;

    @Nullable
    private String s3ClientName;

    @NonNull
    private String bucketName;

    public DefaultS3StorageConfigurationProvider(@Parameter String name) {
        this.name = name;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    @NonNull
    public Class<?> getRootClass() {
        return this.rootClass;
    }

    /**
     * Class of the Root Instance.
     * <a href="https://docs.microstream.one/manual/storage/root-instances.html">Root Instances</a>
     * @param rootClass Class for the Root Instance.
     */
    public void setRootClass(@NonNull Class<?> rootClass) {
        this.rootClass = rootClass;
    }

    @Override
    @NonNull
    public Optional<String> getS3ClientName() {
        return Optional.ofNullable(s3ClientName);
    }

    /**
     * Name of the s3 client to use.  If unset the client with the name of this storage will be used if available.
     * @param s3ClientName the name of the S3 client to use
     */
    public void setS3ClientName(@Nullable String s3ClientName) {
        this.s3ClientName = s3ClientName;
    }

    @NonNull
    @Override
    public String getBucketName() {
        return bucketName;
    }

    /**
     * @param bucketName Name of the bucket to use.
     */
    public void setBucketName(@NonNull String bucketName) {
        this.bucketName = bucketName;
    }
}
