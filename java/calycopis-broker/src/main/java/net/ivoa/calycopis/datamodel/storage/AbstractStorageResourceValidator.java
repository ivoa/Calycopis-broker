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

package net.ivoa.calycopis.datamodel.storage;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;

/**
 * Public interface for StorageResource validators and results.
 * 
 */
public interface AbstractStorageResourceValidator
extends Validator<IvoaAbstractStorageResource, AbstractStorageResourceEntity>
    {
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
     * Validate a component.
     * TODO Return a Result object instead of just a ResultEnum.
     * 
     */
    public ResultEnum validate(
        final IvoaAbstractStorageResource requested,
        final OfferSetRequestParserContext context
        );
    
    /**
     * Bean implementation of a StorageResourceValidator result.
     * 
     */
    @Slf4j
    public static class ResultBean
    extends Validator.ResultBean<IvoaAbstractStorageResource, AbstractStorageResourceEntity>
    implements Result
        {
        /**
         * Public constructor.
         * 
         */
        public ResultBean(final ResultEnum result)
            {
            super(result);
            }

        /**
         * Public constructor.
         * 
         */
        public ResultBean(
            final ResultEnum result,
            final IvoaAbstractStorageResource object
            ){
            super(
                result,
                object,
                (object != null) ? object.getMeta() : null
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
        // Here because we need to create Results with just a status and no entity.
        public AbstractStorageResourceEntity build(final SimpleExecutionSessionEntity session)
            {
            return null ;
            }

        @Override
        public Long getPrepareDuration()
            {
            return 0L;
            }

        @Override
        public Long getReleaseDuration()
            {
            return 0L;
            }
        }   
    }