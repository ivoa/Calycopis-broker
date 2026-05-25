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
package net.ivoa.calycopis.broker.engine.entities.offerset;

import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.AbstractEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.broker.engine.functional.platfom.Platform;
import net.ivoa.calycopis.schema.spring.model.IvoaExecutionRequest;
import net.ivoa.calycopis.schema.spring.model.IvoaOfferSetResponse.ResultEnum;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleExecutionSessionPhase;

/**
 *
 */
@Slf4j
public class OfferSetFactoryImpl
    extends FactoryBaseImpl
    implements OfferSetFactory
    {

    private final Platform platform;
    private final OfferSetRequestParser offersetRequestParser;
    
    private final AbstractEntityRepository<OfferSetEntity> offersetRepository;

    /**
     * Public constructor used by our Platform.
     * 
     */
    public OfferSetFactoryImpl(
        final Platform platform,
        final AbstractEntityRepository<OfferSetEntity> offersetRepository,
        final OfferSetRequestParser offersetParser
        ){
        super();
        this.platform = platform;
        this.offersetRepository = offersetRepository;
        this.offersetRequestParser = offersetParser;
        }

    @Override
    public Optional<OfferSetEntity> select(final UUID uuid)
		{
		return this.offersetRepository.findById(
            uuid
            ); 
		}

    @Override
    public OfferSetEntity create(final IvoaExecutionRequest offersetRequest)
    	{
        //
        // Validate the request. 
        OfferSetRequestParserContext offersetContext = offersetRequestParser.stageOne(
            platform,
            offersetRequest
            );
        //
        // Create the OfferSetEntity from the context.
        return this.create(
            offersetContext,
            0
            );
    	}

    @Override
    public SimpleExecutionSessionEntity direct(final IvoaExecutionRequest executionRequest)
        {
        //
        // Validate the request.
        OfferSetRequestParserContext offersetContext = offersetRequestParser.stageOne(
            platform,
            executionRequest
            );

        //
        // If the request is valid, create a new OfferSetEntity and return the first offer.
        if (offersetContext.valid())
            {
            OfferSetEntity offerSetEntity = this.create(
                offersetContext,
                1
                );
            //
            // If the OfferSetEntity is valid.
            if (offerSetEntity.getResult() == ResultEnum.YES)
                {
                //
                // If the OfferSetEntity has at least one offer.
                Iterator<AbstractExecutionSessionEntity> offers = offerSetEntity.getOfferEntities().iterator();
                if (offers.hasNext())
                    {
                    // TODO Get rid of the nasty class casts.
                    SimpleExecutionSessionEntity offer = (SimpleExecutionSessionEntity) offers.next();
                    //
                    // Set the phase to ACCEPTED and schedule a PrepareSessionRequest for the offer.
                    offer.setPhase(
                        IvoaSimpleExecutionSessionPhase.ACCEPTED
                        );
                    platform.getProcessingRequestFactory().getSessionProcessingRequestFactory().createPrepareSessionRequest(
                        offer
                        );
                    return offer;
                    }
                }
            }
        //
        // If the request is not valid, return a FAILED ExecutionSessionEntity. 
        SimpleExecutionSessionEntity failed = new SimpleExecutionSessionEntity();
        failed.setPhase(
            IvoaSimpleExecutionSessionPhase.FAILED
            );
        failed.claimMessages(
            offersetContext.getMessages()
            );
        return failed;
        }

    protected OfferSetEntity create(final OfferSetRequestParserContext offersetContext, int offerCount)
        {
        //
        // Create a new OfferSetEntity.
        OfferSetEntity offersetEntity = new OfferSetEntity(
            // tempfix    
            // offersetRequest.getName(),
            // offersetRequest.getDescription(),
            null,
            null,
            Instant.now(),
            Instant.now().plusSeconds(
                DEFAULT_EXPIRY_TIME_SECONDS
                )
            );
        //
        // Save the OfferSet before we add any offers.
        this.offersetRepository.save(
            offersetEntity
            );
        //
        // Add the offers to the OfferSetEntity.
        offersetRequestParser.stageTwo(
            platform,
            offersetEntity,
            offersetContext,
            offerCount
            );
        //
        // Save the OfferSet and the offers.
        return this.offersetRepository.save(
            offersetEntity
            );
        }
    }

