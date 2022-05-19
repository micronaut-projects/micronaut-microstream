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
package io.micronaut.microstream.health;

import io.micronaut.core.annotation.Introspected;

/**
 * Health information about a Microstream instance.
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Introspected
public class MicroStreamHealth {

    private final boolean startingUp;
    private final boolean running;
    private final boolean active;
    private final boolean acceptingTasks;
    private final boolean shuttingDown;
    private final boolean shutdown;

    /**
     *
     * @param startingUp Whether the Microstream instance is starting up.
     * @param running Whether the Microstream instance is running
     * @param active Whether the Microstream instance is active
     * @param acceptingTasks Whether the Microstream instance accepts tasks
     * @param shuttingDown Whether the Microstream instance is shutting down
     * @param shutdown Whether the Microstream instance is off
     */
    public MicroStreamHealth(boolean startingUp,
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
     * @return Whether the Microstream instance is starting up.
     */
    public boolean isStartingUp() {
        return startingUp;
    }

    /**
     *
     * @return Whether the Microstream instance is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     *
     * @return Whether the Microstream instance is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     *
     * @return Whether the Microstream instance accepts tasks
     */
    public boolean isAcceptingTasks() {
        return acceptingTasks;
    }

    /**
     *
     * @return Whether the Microstream instance is shutting down
     */
    public boolean isShuttingDown() {
        return shuttingDown;
    }

    /**
     *
     * @return Whether the Microstream instance is off
     */
    public boolean isShutdown() {
        return shutdown;
    }
}
