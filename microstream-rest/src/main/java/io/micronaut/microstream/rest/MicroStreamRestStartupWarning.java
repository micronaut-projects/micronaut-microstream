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
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.annotation.Internal;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Internal
@Requires(notEnv = Environment.TEST)
final class MicroStreamRestStartupWarning implements ApplicationEventListener<StartupEvent> {

    static final String WARNING_MESSAGE = """
        *****************

        \tThe MicroStream REST endpoint is enabled and you are not running in a test environment.
        \tThis endpoint is not intended for production use as it may allow data to be exfiltrated.
        \tPlease see https://micronaut-projects.github.io/micronaut-microstream/snapshot/guide/#rest for instruction on how to disable.""";

    private static final Logger LOG = LoggerFactory.getLogger(MicroStreamRestStartupWarning.class);

    @Override
    public void onApplicationEvent(StartupEvent event) {
        LOG.warn(WARNING_MESSAGE);
    }
}
