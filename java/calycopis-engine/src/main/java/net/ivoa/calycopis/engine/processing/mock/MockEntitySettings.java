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
 *     "timestamp": "2026-04-29T10:00:00",
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

package net.ivoa.calycopis.engine.processing.mock;

/**
 * Plain-POJO settings for mock entity processing actions.
 *
 * Delay values are in seconds.  Use the {@code getXxxDelayMillis()} methods
 * to get the delay in milliseconds for use with {@code Thread.sleep()}.
 *
 * This class carries no Spring dependency.  The broker-layer subclass
 * {@code MockEntitySettings} in the broker module applies the
 * {@code @Component} and {@code @ConfigurationProperties} annotations so
 * that values can be overridden from {@code application.yaml}.
 */
public class MockEntitySettings
    {

    private Actions actions = new Actions();

    public Actions getActions()
        {
        return actions;
        }

    public void setActions(Actions actions)
        {
        this.actions = actions;
        }

    public int getPrepareDelayMillis()
        {
        return actions.getPrepare().getDelay() * 1000;
        }

    public int getMonitorDelayMillis()
        {
        return actions.getMonitor().getDelay() * 1000;
        }

    public int getMonitorCount()
        {
        return actions.getMonitor().getCount();
        }

    public int getReleaseDelayMillis()
        {
        return actions.getRelease().getDelay() * 1000;
        }

    public int getCancelDelayMillis()
        {
        return actions.getCancel().getDelay() * 1000;
        }

    public int getFailDelayMillis()
        {
        return actions.getFail().getDelay() * 1000;
        }

    public static class Actions
        {

        private PrepareSettings prepare = new PrepareSettings();
        private MonitorSettings monitor = new MonitorSettings();
        private ReleaseSettings release = new ReleaseSettings();
        private CancelSettings cancel  = new CancelSettings();
        private FailSettings    fail    = new FailSettings();

        public PrepareSettings getPrepare()
            {
            return prepare;
            }

        public void setPrepare(PrepareSettings prepare)
            {
            this.prepare = prepare;
            }

        public MonitorSettings getMonitor()
            {
            return monitor;
            }

        public void setMonitor(MonitorSettings monitor)
            {
            this.monitor = monitor;
            }

        public ReleaseSettings getRelease()
            {
            return release;
            }

        public void setRelease(ReleaseSettings release)
            {
            this.release = release;
            }

        public CancelSettings getCancel()
            {
            return cancel;
            }

        public void setCancel(CancelSettings cancel)
            {
            this.cancel = cancel;
            }

        public FailSettings getFail()
            {
            return fail;
            }

        public void setFail(FailSettings fail)
            {
            this.fail = fail;
            }

        }

    public static class PrepareSettings
        {
        private int delay = 30;

        public int getDelay()
            {
            return delay;
            }

        public void setDelay(int delay)
            {
            this.delay = delay;
            }

        }

    public static class MonitorSettings
        {
        private int delay = 30;
        private int count = 4;

        public int getDelay()
            {
            return delay;
            }

        public void setDelay(int delay)
            {
            this.delay = delay;
            }

        public int getCount()
            {
            return count;
            }

        public void setCount(int count)
            {
            this.count = count;
            }

        }

    public static class ReleaseSettings
        {
        private int delay = 30;

        public int getDelay()
            {
            return delay;
            }

        public void setDelay(int delay)
            {
            this.delay = delay;
            }

        }

    public static class CancelSettings
        {
        private int delay = 30;

        public int getDelay()
            {
            return delay;
            }

        public void setDelay(int delay)
            {
            this.delay = delay;
            }

        }

    public static class FailSettings
        {
        private int delay = 30;

        public int getDelay()
            {
            return delay;
            }

        public void setDelay(int delay)
            {
            this.delay = delay;
            }

        }

    }
