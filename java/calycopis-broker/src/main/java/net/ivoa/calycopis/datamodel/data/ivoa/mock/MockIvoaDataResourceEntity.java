/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.data.ivoa.mock;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.data.ivoa.IvoaDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
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
@Entity
@Table(
    name = "mockivoadataresources"
    )
public class MockIvoaDataResourceEntity
    extends IvoaDataResourceEntity
    implements MockIvoaDataResource
    {
    /**
     * 
     */
    public MockIvoaDataResourceEntity()
        {
        super();
        }

    /**
     *
     */
    public MockIvoaDataResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        super(
            session,
            storage,
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
