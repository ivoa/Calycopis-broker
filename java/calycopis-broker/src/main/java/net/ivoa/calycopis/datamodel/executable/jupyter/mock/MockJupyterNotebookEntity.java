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
 *     "timestamp": "2026-04-14T17:00:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 10,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.executable.jupyter.mock;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.platfom.mock.MockPlatform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.functional.processing.mock.MockEntitySettings;
import net.ivoa.calycopis.functional.processing.mock.MockMonitorAction;
import net.ivoa.calycopis.functional.processing.mock.MockPrepareAction;
import net.ivoa.calycopis.functional.processing.mock.MockReleaseAction;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "mockjupyternotebooks"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class MockJupyterNotebookEntity
extends JupyterNotebookEntity
implements MockJupyterNotebook
    {
    public MockJupyterNotebookEntity()
        {
        super();
        }

    public MockJupyterNotebookEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        super(
            session,
            result
            );
        }

    @Override
    public ProcessingAction getPrepareAction(final Platform platform, final ComponentProcessingRequest request)
        {
        MockEntitySettings settings = ((MockPlatform) platform).getMockEntitySettings();
        return new MockPrepareAction(
            this,
            settings.getPrepareDelayMillis()
            );
        }

    @Override
    public ProcessingAction getMonitorAction(Platform platform, ComponentProcessingRequest request)
        {
        MockEntitySettings settings = ((MockPlatform) platform).getMockEntitySettings();
        if (this.lifecycleLoopCount < 0)
            {
            this.lifecycleLoopCount = settings.getMonitorCount();
            }
        return new MockMonitorAction(
            this,
            settings.getMonitorDelayMillis()
            );
        }

    @Override
    public ProcessingAction getReleaseAction(final Platform platform, final ComponentProcessingRequest request)
        {
        MockEntitySettings settings = ((MockPlatform) platform).getMockEntitySettings();
        return new MockReleaseAction(
            this,
            settings.getReleaseDelayMillis()
            );
        }

    int lifecycleLoopCount = -1 ;
    
    @Override
    public int getLifecycleLoopCount()
        {
        return lifecycleLoopCount ;
        }

    @Override
    public void setLifecycleLoopCount(int count)
        {
        this.lifecycleLoopCount = count;
        }
    }
