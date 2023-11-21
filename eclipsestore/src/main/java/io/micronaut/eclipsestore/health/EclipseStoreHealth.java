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
package io.micronaut.eclipsestore.health;

import io.micronaut.core.annotation.Introspected;

/**
 * Health information about a EclipseStore instance.
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Introspected
public class EclipseStoreHealth {

    private final boolean startingUp;
    private final boolean running;
    private final boolean active;
    private final boolean acceptingTasks;
    private final boolean shuttingDown;
    private final boolean shutdown;

    /**
     *
     * @param startingUp Whether the EclipseStore instance is starting up.
     * @param running Whether the EclipseStore instance is running
     * @param active Whether the EclipseStore instance is active
     * @param acceptingTasks Whether the EclipseStore instance accepts tasks
     * @param shuttingDown Whether the EclipseStore instance is shutting down
     * @param shutdown Whether the EclipseStore instance is off
     */
    public EclipseStoreHealth(boolean startingUp,
                             boolean running,
                             boolean active,
                             boolean acceptingTasks,
                             boolean shuttingDown,
                             boolean shutdown) {
        this.startingUp = startingUp;
        this.running = running;
        this.active = active;
        this.acceptingTasks = acceptingTasks;
        this.shuttingDown = shuttingDown;
        this.shutdown = shutdown;
    }

    /**
     *
     * @return Whether the EclipseStore instance is starting up.
     */
    public boolean isStartingUp() {
        return startingUp;
    }

    /**
     *
     * @return Whether the EclipseStore instance is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     *
     * @return Whether the EclipseStore instance is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     *
     * @return Whether the EclipseStore instance accepts tasks
     */
    public boolean isAcceptingTasks() {
        return acceptingTasks;
    }

    /**
     *
     * @return Whether the EclipseStore instance is shutting down
     */
    public boolean isShuttingDown() {
        return shuttingDown;
    }

    /**
     *
     * @return Whether the EclipseStore instance is off
     */
    public boolean isShutdown() {
        return shutdown;
    }
}
