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
package io.micronaut.eclipsestore.conf;

import io.micronaut.context.annotation.ConfigurationProperties;

/**
 * Configuration for EclipseStore module.
 * {@link EclipseStoreConfiguration} and {@link EclipseStoreConfigurationProperties} exist to generate configuration reference documentation automatically.
 * @author Sergio del Amo
 * @since 1.0.0
 */
@ConfigurationProperties(EclipseStoreConfigurationProperties.PREFIX)
public class EclipseStoreConfigurationProperties implements EclipseStoreConfiguration {
    /**
     * ConfigurationPrefix.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String PREFIX = "eclipsestore";

    /**
     * Whether EclipseStore module is enabled.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_ENABLED = true;

    private boolean enabled = DEFAULT_ENABLED;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Whether EclipseStore module is enabled. Default Value: {@value EclipseStoreConfigurationProperties#DEFAULT_ENABLED}
     * @param enabled Whether this module is enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
