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
 * Based on
 * https://www.geeksforgeeks.org/spring-boot-jparepository-with-example/
 * https://howtodoinjava.com/spring-boot/spring-boot-jparepository-example/
 *
 */
package net.ivoa.calycopis.datamodel.offerset;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.spring.model.IvoaExecutionRequest;
import net.ivoa.calycopis.spring.model.IvoaOfferSetResponse.ResultEnum;
import net.ivoa.calycopis.engine.session.ExecutionSessionPhase;

/**
 * Service for responding to OfferSet requests.
 *
 */
@Slf4j
@Component
public class OfferSetFactoryImpl
    extends FactoryBaseImpl
    implements OfferSetFactory
    {

    private final Platform platform;
    private final OfferSetRequestParser offersetRequestParser;
    
    private final OfferSetRepository offersetRepository;

    private final ProcessingRequestFactory processingRequestFactory;
    
    @Autowired
    public OfferSetFactoryImpl(
        final Platform platform,
        final OfferSetRepository offersetRepository,
        final OfferSetRequestParser offersetParser,
        final ProcessingRequestFactory processingRequestFactory
        ){
        super();
        this.platform = platform;
        this.platform.initialize();
        this.offersetRepository = offersetRepository;
        this.offersetRequestParser = offersetParser;
        this.processingRequestFactory = processingRequestFactory;
        }

    /**
     * Select an OfferSet based on its identifier.
     *
     */
    @Override
    public Optional<OfferSetEntity> select(final UUID uuid)
		{
        log.debug("select(UUID)");
        log.debug("UUID [{}]", uuid);
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
    public OfferSetEntity create(final OfferSetRequestParserContext offersetContext, int offerCount)
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
                if (offerSetEntity.getOffers().size() > 0)
                    {
                    // TODO Get rid of the nasty class casts.
                    SimpleExecutionSessionEntity offer = (SimpleExecutionSessionEntity) offerSetEntity.getOffers().getFirst();
                    //
                    // Set the phase to ACCEPTED and schedule a PrepareSessionRequest for the offer.
                    offer.setPhase(
                        ExecutionSessionPhase.ACCEPTED
                        );
                    processingRequestFactory.getSessionProcessingRequestFactory().createPrepareSessionRequest(
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
            ExecutionSessionPhase.FAILED
            );
        failed.claimMessages(
            offersetContext.getMessages()
            );
        return failed;
        }
    }

