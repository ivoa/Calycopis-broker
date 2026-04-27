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
 *
 */

package net.ivoa.calycopis.datamodel.session.simple;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.spring.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.spring.model.IvoaEnumValueUpdate;
import net.ivoa.calycopis.engine.session.ExecutionSessionPhase;

/**
 * 
 */
@Slf4j
@Component
public class SimpleExecutionSessionEntityUpdateHandlerImpl
extends FactoryBaseImpl
implements SimpleExecutionSessionEntityUpdateHandler
    {

    private final Platform platform;
    private final ProcessingRequestFactory processing; 
    
    private final SimpleExecutionSessionEntityRepository sessionRepository;
    
    @Autowired
    public SimpleExecutionSessionEntityUpdateHandlerImpl(
        final SimpleExecutionSessionEntityRepository repository,
        final ProcessingRequestFactory processing,
        final Platform platform
        ){
        super();
        this.sessionRepository = repository;
        this.processing = processing;
        this.platform = platform;
        }

    @Override
    public Optional<SimpleExecutionSessionEntity> update(final UUID uuid, final IvoaAbstractUpdate update)
        {
        log.debug("update(UUID)");
        log.debug("UUID   [{}]", uuid);
        log.debug("Update [{}]", update.getClass());

        Optional<SimpleExecutionSessionEntity> result = this.sessionRepository.findById(
            uuid
            );
        if (result.isEmpty())
            {
            log.warn("Session not found [{}]", uuid);
            return result ;
            }
        else {
            SimpleExecutionSessionEntity entity = update(
                result.get(),
                update
                );  
            // Do we need this ?
            // The Sessions set to REJECTED are saved in the database too.
            entity = this.sessionRepository.save(
                entity
                );
            return Optional.of(
                entity
                );
            }
        }

    protected SimpleExecutionSessionEntity update(SimpleExecutionSessionEntity entity , final IvoaAbstractUpdate update)
        {
        log.debug("update(Entity, Update)");
        log.debug("Entity [{}]", entity.getUuid());
        log.debug("Update [{}]", update.getClass());
        switch(update)
            {
            case IvoaEnumValueUpdate valueupdate :
                entity = this.update(
                    entity,
                    valueupdate
                    );
                break ;

            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Unknown update class [{}]", update.getClass().getName());
                break ;
            }
        return entity ;
        }

    protected SimpleExecutionSessionEntity update(SimpleExecutionSessionEntity entity , final IvoaEnumValueUpdate update)
        {
        log.debug("update(Entity, EnumValueUpdate)");
        log.debug("Entity [{}][{}]", entity.getUuid(), entity.getPhase());
        log.debug("Update [{}][{}]", update.getPath(), update.getValue());
        switch(update.getPath())
            {
            case "phase"  :
            case "/phase" :
                try {
                    ExecutionSessionPhase newphase = ExecutionSessionPhase.fromValue(
                        update.getValue()
                        );
                    entity = this.update(
                        entity,
                        newphase
                        );
                    }
                catch (IllegalArgumentException ouch)
                    {
                    log.warn("Invalid update value [{}]", update.getValue());
                    }
                break ;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Unknown phase path [{}]", update.getPath());
                break ;
            }
        return entity ;
        }

    protected SimpleExecutionSessionEntity update(SimpleExecutionSessionEntity entity , final ExecutionSessionPhase newphase)
        {
        log.debug("update(Entity, Phase) [{}][{}][{}]", entity.getUuid(), entity.getPhase(), newphase);
        switch(newphase)
            {
            case ACCEPTED:
                entity = accept(
                    entity
                    );
                break ;
            case REJECTED:
                entity = reject(
                    entity
                    );
                break ;
            case CANCELLED:
                entity = cancel(
                    entity
                    );
                break ;
            case FAILED:
                entity = fail(
                    entity
                    );
                break ;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), newphase);
                break ;
            }
        return entity ;
        }

    protected SimpleExecutionSessionEntity accept(SimpleExecutionSessionEntity entity)
        {
        log.debug("accept(Entity, Phase) [{}][{}]", entity.getUuid(), entity.getPhase());
        switch(entity.getPhase())
            {
            case OFFERED:
                entity.setPhase(
                    ExecutionSessionPhase.ACCEPTED
                    );
                //
                // REJECT the other Sessions in the offer.
                for (AbstractExecutionSessionEntity sibling : entity.getOfferSet().getOffers())
                    {
                    if (sibling.getUuid().equals(entity.getUuid()) == false)
                        {
                        if (sibling instanceof SimpleExecutionSessionEntity)
                            {
                            reject(
                                (SimpleExecutionSessionEntity) sibling
                                );
                            }
                        }
                    }

                log.debug("Creating prepare request for session [{}]", entity.getUuid());
                processing.getSessionProcessingRequestFactory().createPrepareSessionRequest(
                    entity
                    );

                break;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), ExecutionSessionPhase.ACCEPTED);
                break;
            }
        return entity ;
        }

    protected SimpleExecutionSessionEntity reject(SimpleExecutionSessionEntity entity)
        {
        log.debug("reject(Entity, Phase) [{}][{}]", entity.getUuid(), entity.getPhase());
        switch(entity.getPhase())
            {
            case OFFERED:
                entity.setPhase(
                    ExecutionSessionPhase.REJECTED
                    );
                break;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), ExecutionSessionPhase.REJECTED);
                break;
            }
        return entity ;
        }

    protected SimpleExecutionSessionEntity cancel(SimpleExecutionSessionEntity entity)
        {
        log.debug("cancel(Entity, Phase) [{}][{}]", entity.getUuid(), entity.getPhase());
        switch(entity.getPhase())
            {
            case INITIAL:
            case OFFERED:
            case ACCEPTED:
            case WAITING:
            case PREPARING:
            case AVAILABLE:
            case RUNNING:
                entity.setPhase(
                    ExecutionSessionPhase.CANCELLED
                    );
                break;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), ExecutionSessionPhase.CANCELLED);
                break;
            }
        return entity ;
        }

    // TODO This should require a reason.
    protected SimpleExecutionSessionEntity fail(SimpleExecutionSessionEntity entity)
        {
        log.debug("fail(Entity, Phase) [{}][{}]", entity.getUuid(), entity.getPhase());
        switch(entity.getPhase())
            {
            case INITIAL:
            case OFFERED:
            case ACCEPTED:
            case WAITING:
            case PREPARING:
            case AVAILABLE:
            case RUNNING:
            case RELEASING:
                // If a sessions fails during releasing - has it actually failed ?
                entity.setPhase(
                    ExecutionSessionPhase.FAILED
                    );
                break;
            default:
                // We need to be able to return some error messages here.
                // We need an ErrorResponse structure ..
                log.warn("Invalid phase transition [{}][{}][{}]", entity.getUuid(), entity.getPhase(), ExecutionSessionPhase.FAILED);
                break;
            }
        return entity ;
        }
    }
