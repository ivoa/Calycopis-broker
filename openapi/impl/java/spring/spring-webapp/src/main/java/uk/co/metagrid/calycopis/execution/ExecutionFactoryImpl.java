/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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

package uk.co.metagrid.calycopis.execution;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionStatus;
import uk.co.metagrid.calycopis.factory.FactoryBaseImpl;
import uk.co.metagrid.calycopis.offers.OfferBlock;
import uk.co.metagrid.calycopis.offerset.OfferSetEntity;
import uk.co.metagrid.calycopis.offerset.OfferSetRequestParser;

/**
 * An Execution Factory implementation.
 *
 */
@Slf4j
@Component
public class ExecutionFactoryImpl
    extends FactoryBaseImpl
    implements ExecutionFactory
    {

    private final ExecutionRepository repository;

    @Autowired
    public ExecutionFactoryImpl(final ExecutionRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<ExecutionEntity> select(UUID uuid)
        {
        log.debug("select(UUID)");
        log.debug("UUID [{}]", uuid);
        return this.repository.findById(
            uuid
            );
        }

    @Override
    public ExecutionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParser context)
        {
        return this.create(
            offerblock,
            parent,
            context,
            IvoaExecutionSessionStatus.OFFERED,
            true
            );
        }
    
    @Override
    public ExecutionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParser context, final IvoaExecutionSessionStatus state)
        {
        return this.create(
            offerblock,
            parent,
            context,
            state,
            true
            );
        }
    
    @Override
    public ExecutionEntity create(final OfferBlock offerblock, final OfferSetEntity parent, final OfferSetRequestParser context, final IvoaExecutionSessionStatus state, boolean save)
        {
        ExecutionEntity created = new ExecutionEntity(
            offerblock,
            parent,
            context,
            state
            );
        log.debug("created [{}]", created.getUuid());
        if (save)
            {
            created = this.repository.save(created);
            log.debug("created [{}]", created.getUuid());
            }
        return created;
        }
    }

