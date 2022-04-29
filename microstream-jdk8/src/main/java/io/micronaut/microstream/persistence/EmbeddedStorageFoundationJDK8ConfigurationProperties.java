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
package io.micronaut.microstream.persistence;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.core.util.Toggleable;

/**
 * Configuration properties for the {@link EmbeddedStorageFoundationJDK8TypeHandlers}.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Requires(property = EmbeddedStorageFoundationJDK8ConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE, defaultValue = StringUtils.TRUE)
@ConfigurationProperties(EmbeddedStorageFoundationJDK8ConfigurationProperties.PREFIX)
public class EmbeddedStorageFoundationJDK8ConfigurationProperties implements Toggleable {

    public static final String PREFIX = "microstream.persistence.type-handlers.jdk8";

    /**
     * The default enable value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_ENABLED = true;

    private boolean enabled = DEFAULT_ENABLED;

    /**
     * @return true if {@link EmbeddedStorageFoundationJDK8TypeHandlers} is enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Enables {@link EmbeddedStorageFoundationJDK8TypeHandlers}. Default value {@value #DEFAULT_ENABLED}
     * @param enabled True if it is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
