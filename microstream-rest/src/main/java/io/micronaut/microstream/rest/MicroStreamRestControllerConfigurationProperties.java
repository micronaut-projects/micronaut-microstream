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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import javax.validation.constraints.NotBlank;

/**
 * Configuration properties for the {@link MicroStreamRestController}.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@ConfigurationProperties(MicroStreamRestControllerConfigurationProperties.PREFIX)
public class MicroStreamRestControllerConfigurationProperties implements MicroStreamRestControllerConfiguration {

    public static final String PREFIX = "microstream.rest";

    /**
     * The default enable value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_ENABLED = true;

    /**
     * The default path.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_PATH = "microstream";

    private boolean enabled = DEFAULT_ENABLED;

    @NonNull
    @NotBlank
    private String path = DEFAULT_PATH;

    /**
     * @return true if you want to enable the {@link MicroStreamRestController}
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * @return path to the {@link MicroStreamRestController}. Default value {@value #DEFAULT_PATH}
     */
    @NonNull
    @Override
    public String getPath() {
        return this.path;
    }

    /**
     * Enables {@link MicroStreamRestController}. Default value {@value #DEFAULT_ENABLED}
     * @param enabled True if it is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Path to the {@link MicroStreamRestController}. Default value {@value #DEFAULT_PATH}
     * @param path The path
     */
    public void setPath(@NonNull String path) {
        if (StringUtils.isNotEmpty(path)) {
            this.path = path;
        }
    }
}
