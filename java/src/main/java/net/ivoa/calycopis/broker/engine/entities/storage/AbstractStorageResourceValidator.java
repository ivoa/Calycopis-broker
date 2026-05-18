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
 *     "timestamp": "2026-02-17T13:20:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 1,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.entities.storage;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.validator.Validator;
import net.ivoa.calycopis.schema.spring.model.IvoaAbstractStorageResource;

/**
 * Public interface for AbstractStorageResource Validators.
 * 
 */
public interface AbstractStorageResourceValidator
extends Validator<IvoaAbstractStorageResource, AbstractStorageResourceEntity>
    {

    /**
     * Validate a component.
     * 
     */
    public ResultEnum validate(
        final IvoaAbstractStorageResource requested,
        final OfferSetRequestParserContext context
        );
    
    /**
     * Public interface for a validator result.
     * 
     */
    public interface Result
    extends Validator.Result<IvoaAbstractStorageResource, AbstractStorageResourceEntity> 
        {

        /**
         * Add a data resource validation result.
         * 
         */
        public void addDataResourceResult(final AbstractDataResourceValidator.Result result);
        
        /**
         * Get a List of the validated data resources associated with this storage resource.
         * 
         */
        public List<AbstractDataResourceValidator.Result> getDataResourceResults();

        /**
         * Build an entity based on our validation result.
         * 
         */
        public AbstractStorageResourceEntity build(final SimpleExecutionSessionEntity session);

        }

    /**
     * Bean implementation of a validator result.
     * 
     */
    @Slf4j
    public static class ResultBean
    extends Validator.ResultBean<IvoaAbstractStorageResource, AbstractStorageResourceEntity>
    implements Result
        {
        /**
         * Protected constructor with just a ResultEnum.
         * Used to respond to a failed validation, where we don't have an object to return.
         * 
         */
        public ResultBean(final ResultEnum result)
            {
            super(result);
            }

        /**
         * Protected constructor with a ResultEnum and IvoaAbstractDataResource.
         * 
         */
        protected ResultBean(
            final ResultEnum result,
            final IvoaAbstractStorageResource object
            ){
            super(
                result,
                object,
                object.getMeta()
                );
            }
        
        private List<AbstractDataResourceValidator.Result> dataResourceResults = new ArrayList<AbstractDataResourceValidator.Result>();
        @Override
        public void addDataResourceResult(AbstractDataResourceValidator.Result result)
            {
            this.dataResourceResults.add(
                result
                );
            }
        @Override
        public List<AbstractDataResourceValidator.Result> getDataResourceResults()
            {
            return this.dataResourceResults;
            }

        @Override
        public Long getTotalPrepareDuration()
            {
            log.debug("AbstractStorageResourceValidator.getTotalPrepareTime() [{}]", this.getName());
            
            Long maxDataPrepareTime = 0L;
            for (AbstractDataResourceValidator.Result dataResult : this.getDataResourceResults())
                {
                Long dataPrepareTime = dataResult.getPrepareDuration();
                log.debug("Data prepare time [{}][{}]", dataResult.getName(), dataPrepareTime);
                if ((dataPrepareTime != null) && (dataPrepareTime > maxDataPrepareTime))
                    {
                    maxDataPrepareTime = dataPrepareTime;
                    }
                }
            return this.getPrepareDuration() + maxDataPrepareTime;
            }

        @Override
        public Long getPrepareDuration()
            {
            return null;
            }

        @Override
        public Long getReleaseDuration()
            {
            return null;
            }
        }   
    }
