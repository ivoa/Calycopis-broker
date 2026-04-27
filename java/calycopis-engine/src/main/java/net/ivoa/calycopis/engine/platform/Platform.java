/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2026 University of Manchester.
 *
 *     This information is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This information is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-04-27T13:46:34",
 *     "name": "Copilot",
 *     "version": "unknown",
 *     "model": "claude-sonnet-4.5",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.engine.platform;

/**
 * Framework-independent marker interface for an execution platform.
 *
 * A platform provides a family of factories, validators, and repositories
 * that supply platform-specific implementations for the core engine types
 * (sessions, compute resources, executables, storage, data, volumes).
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.functional.platfom.Platform} interface.
 * Concrete broker implementations (e.g. {@code MockPlatformImpl},
 * {@code DockerPlatformImpl}) extend the broker-layer {@code Platform}
 * interface, which in turn extends this engine interface.
 *
 * The factory accessor methods are defined in the broker-layer {@code Platform}
 * interface because they return broker-specific factory types.  Those types
 * will be replaced by engine interfaces in a later migration step once the
 * entity and validator hierarchies have been fully moved to the engine module.
 */
public interface Platform
    {
    /**
     * Initialise the platform (register validators, configure repositories, etc.).
     *
     */
    public void initialize();
    }
