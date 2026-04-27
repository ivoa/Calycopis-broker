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
 *     "timestamp": "2026-02-17T07:10:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 3,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-17T13:20:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 3,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.datamodel.executable.jupyter;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.validator.AbstractValidatorImpl;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.spring.model.IvoaJupyterNotebook;

/**
 * A validator implementation to handle JupyterNotebooks.
 * 
 */
@Slf4j
public abstract class JupyterNotebookValidatorImpl
extends AbstractValidatorImpl<IvoaAbstractExecutable, AbstractExecutableEntity>
implements JupyterNotebookValidator
    {
    
    private final JupyterNotebookEntityFactory entityFactory;

    @Autowired
    public JupyterNotebookValidatorImpl(final JupyterNotebookEntityFactory entityFactory)
        {
        this.entityFactory = entityFactory;
        }
    
    @Override
    public ResultEnum validate(
        final IvoaAbstractExecutable requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractExecutable)");
        log.debug("Executable [{}][{}]", requested.getMeta(), requested.getClass().getName());
        //
        // Use exact class matching rather than instanceof to ensure each
        // validator only handles its specific type, not subclass types.
        // This prevents a parent type's validator from intercepting requests
        // that should be handled by a more specific subclass validator.
        if (requested.getClass() == IvoaJupyterNotebook.class)
            {
            return validate(
                (IvoaJupyterNotebook) requested,
                context
                );
            }
        return ResultEnum.CONTINUE;
        }

    /**
     * Validate an IvoaJupyterNotebook.
     *
     */
    public ResultEnum validate(
        final IvoaJupyterNotebook requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaJupyterNotebook)");
        log.debug("Executable [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;

        IvoaJupyterNotebook validated = new IvoaJupyterNotebook()
            .kind(JupyterNotebook.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );

        //
        // Validate the notebook location.
        success &= validateLocation(
            requested.getLocation(),
            validated,
            context
            );
        
        //
        // Everything is good, create a validator Result.
        if (success)
            {
            context.setExecutableResult(
                new AbstractExecutableValidator.ResultBean(
                    Validator.ResultEnum.ACCEPTED,
                    validated
                    ){
                    @Override
                    public AbstractExecutableEntity build(final SimpleExecutionSessionEntity session)
                        {
                        this.entity = JupyterNotebookValidatorImpl.this.entityFactory.create(
                            session,
                            this
                            );
                        return this.entity;
                        }
    
                    @Override
                    public Long getPrepareDuration()
                        {
                        return JupyterNotebookValidatorImpl.this.getPrepareDuration(
                            validated
                            );
                        }
    
                    @Override
                    public Long getReleaseDuration()
                        {
                        return JupyterNotebookValidatorImpl.this.getReleaseDuration(
                            validated
                            );
                        }
                    }
                );
            return ResultEnum.ACCEPTED;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            context.valid(false);
            return ResultEnum.FAILED;
            }
        }

    /**
     * Apply any platform specific validation rules.
     * 
     */
    protected abstract boolean validateLocation(final String location, final OfferSetRequestParserContext context);

    /**
     * Validate the notebook location.
     * 
     */
    public boolean validateLocation(
        final String requested,
        final IvoaJupyterNotebook validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateLocation(String ...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;

        String location = notEmpty(
            requested
            );
        if ((location == null) || (location.isEmpty()))
            {
            context.addWarning(
                "uri:missing-required-value",
                "JupyterNotebook - location is required"
                );
            success = false ;
            }
        else {
            success &= validateLocation(
                location,
                context
                );
            }

        if (success)
            {
            validated.setLocation(
                location
                );
            }
        
        return success;
        }

    /**
     * Get the prepare duration for a resource.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long getPrepareDuration(final IvoaJupyterNotebook validated);

    /**
     * Get the release duration for a resource.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long getReleaseDuration(final IvoaJupyterNotebook validated);
    
    }

