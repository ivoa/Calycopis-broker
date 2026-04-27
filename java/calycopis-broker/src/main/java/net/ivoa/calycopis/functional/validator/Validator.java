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
package net.ivoa.calycopis.functional.validator;

import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;

/**
 * Public interface for a Validator.
 *  
 */
public interface Validator<ObjectType, EntityType extends ComponentEntity>
    {
    /**
     * Result enum for the validation process.
     * CONTINUE means this validator didn't recognise the object. 
     * ACCEPTED means this validator recognised and validated the object. 
     * FAILED means this validator recognised but failed the object.
     * 
     */
    enum ResultEnum{
        CONTINUE(),
        ACCEPTED(),
        FAILED();
        }

    /**
     * Public interface for a validation result.
     * TODO Add the context identifier for the object. 
     * 
     */
    public static interface Result<ObjectType, EntityType>
        {
        /**
         * Get the object identifier.
         * 
         */
        public String getName();

        /**
         * Get the validation result enum.
         * 
         */
        public ResultEnum getEnum();

        /**
         * Get the validated object.
         * 
         */
        public ObjectType getObject();
        
        /**
         * Get the corresponding Entity.
         * 
         */
        public EntityType getEntity();

        /**
         * Get the IVOA metadata for this component.
         * 
         */
        public IvoaComponentMetadata getMeta();
        
        /**
         * Get the preparation duration for this resource.
         *  
         */
        public Long getPrepareDuration();

        /**
         * Get the total preparation duration for this resource.
         *  
         */
        public Long getTotalPrepareDuration();

        /**
         * Get the release duration for this resource.
         *  
         */
        public Long getReleaseDuration();
        
        }

    /**
     * Validate a component.
     * TODO Return a Result object instead of just a ResultEnum.
     *
     */
    public ResultEnum validate(
        final ObjectType requested,
        final OfferSetRequestParserContext context
        );
    
    /**
     * Simple bean implementation of Result.
     *  
     */
    public abstract static class ResultBean<ObjectType, EntityType extends ComponentEntity>
    implements Result<ObjectType, EntityType>
        {
        public ResultBean(final ResultEnum result)
            {
            this(result, null, null);
            }

        public ResultBean(final ResultEnum result, final ObjectType object)
            {
            this(result, object, null);
            }

        public ResultBean(final ResultEnum result, final ObjectType object, final IvoaComponentMetadata meta)
            {
            this.result = result;
            this.object = object;
            this.meta = meta;
            }

        private final ResultEnum result;
        @Override
        public ResultEnum getEnum()
            {
            return this.result;
            }

        private final ObjectType object;
        @Override
        public ObjectType getObject()
            {
            return this.object;
            }
        
        protected EntityType entity;
        @Override
        public EntityType getEntity()
            {
            return this.entity;
            }

        protected IvoaComponentMetadata meta;
        @Override
        public IvoaComponentMetadata getMeta()
            {
            return this.meta;
            }
        
        @Override
        public abstract Long getPrepareDuration();

        @Override
        public abstract Long getReleaseDuration();

        @Override
        public Long getTotalPrepareDuration()
            {
            return getPrepareDuration();
            }

        @Override
        public String getName()
            {
            if (this.entity != null)
                {
                if (this.entity.getName() != null)
                    {
                    return this.entity.getName();
                    }
                else if (this.entity.getUuid() != null)
                    {
                    return this.entity.getUuid().toString();
                    }
                }
            else if (this.meta != null)
                {
                if (this.meta.getName() != null)
                    {
                    return this.meta.getName();
                    }
                else if (this.meta.getUuid() != null)
                    {
                    return this.meta.getUuid().toString();
                    }
                }
            return null;
            }
        }
    }
