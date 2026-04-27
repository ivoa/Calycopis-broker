/**
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
 *     "timestamp": "2026-03-25T14:45:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.datamodel.offerset;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.datamodel.compute.simple.SimpleComputeResource;
import net.ivoa.calycopis.datamodel.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.spring.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaExecutionRequest;
import net.ivoa.calycopis.spring.model.IvoaOfferSetResponse;
import net.ivoa.calycopis.spring.model.IvoaRequestedScheduleBlock;
import net.ivoa.calycopis.spring.model.IvoaRequestedScheduleItem;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeResource;

/**
 *
 */
@Slf4j
@Component
public class OfferSetRequestParserImpl
    extends FactoryBaseImpl
    implements OfferSetRequestParser
    {

    //private final Platform platform ;

    @Autowired
    public OfferSetRequestParserImpl(
        //final Platform platform
        ){
        super();
        //this.platform = platform;
        //this.platform.initialize();
        }

    @Override
    public OfferSetRequestParserContext stageOne(final Platform platform, final IvoaExecutionRequest executionRequest)
        {
        log.debug("process(IvoaOfferSetRequest, OfferSetEntity)");
        OfferSetRequestParserContext offersetContext = new OfferSetRequestParserContextImpl(
            executionRequest
            );
        log.debug("Context valid [{}]", offersetContext.valid());

        //
        // Register all the resources and assign UUIDs.
        offersetContext.registerResources();
        log.debug("Context valid [{}]", offersetContext.valid());

        //
        // Validate the requested storage resources.
        log.debug("Validating the requested storage resources");
        if (executionRequest.getStorage() != null)
            {
            for (IvoaAbstractStorageResource resource : executionRequest.getStorage())
                {
                platform.getStorageResourceValidators().validate(
                    resource,
                    offersetContext
                    );
                }
            }
        log.debug("Context valid [{}]", offersetContext.valid());
        //
        // Validate the requested data resources.
        log.debug("Validating the requested data resources");
        if (executionRequest.getData() != null)
            {
            for (IvoaAbstractDataResource resource : executionRequest.getData())
                {
                platform.getDataResourceValidators().validate(
                    resource,
                    offersetContext
                    );
                }
            }
        log.debug("Context valid [{}]", offersetContext.valid());

        //
        // Validate the requested compute resource.
        // Volume mounts are now validated as part of compute resource validation,
        // since they are children of the compute resource in the schema.
        log.debug("Validating the requested compute resources");
        IvoaAbstractComputeResource computeResource = executionRequest.getCompute();
        if (computeResource == null)
            {
            computeResource = new IvoaSimpleComputeResource()
                .kind(SimpleComputeResource.TYPE_DISCRIMINATOR)
                .meta(
                    new IvoaComponentMetadata().name(
                        "Default compute resource"
                        )
                    .uuid(UUID.randomUUID())
                    );
            }

        log.debug("Context valid [{}]", offersetContext.valid());
        platform.getComputeResourceValidators().validate(
            computeResource,
            offersetContext
            );
        log.debug("Context valid [{}]", offersetContext.valid());

        //
        // Validate the requested executable.
        log.debug("Validating the requested executable");
        IvoaAbstractExecutable executableResource = executionRequest.getExecutable();
        if (executableResource != null)
            {
            platform.getExecutableValidators().validate(
                executableResource,
                offersetContext
                );
            }
        else {
            log.error("Offerset request has no executable");
            offersetContext.addWarning(
                "urn:executable-required",
                "Description of the executable is required"
                );
            offersetContext.valid(false);
            }
        log.debug("Context valid [{}]", offersetContext.valid());

        //
        // Calculate the preparation time.
        offersetContext.calculateTotalPrepareTime();

        //
        // Validate the schedule.
        validate(
            executionRequest.getSchedule(),
            offersetContext
            );
        log.debug("Context valid [{}]", offersetContext.valid());

        return offersetContext;
        }

//
// TODO Move this part to a separate schedule validator.
//

    /**
     * Default session duration, 2 hours.
     *
     */
    public static final Duration DEFAULT_SESSION_DURATION = Duration.ofHours(2);

    /**
     * Default duration for the default start interval.
     * Sometime in the next 2 hours.
     *
     */
    public static final Duration DEFAULT_START_INTERVAL_DURATION = Duration.ofHours(2);

    /**
     * Validate the requested Schedule.
     *
     */
    public boolean validate(final IvoaRequestedScheduleBlock schedule, final OfferSetRequestParserContext context)
        {
        boolean success = true ;

        log.debug("validate(IvoaRequestedScheduleBlock)");

        //
        // Calculate the earliest start time.
        Instant earliestStartTime = Instant.now().plusSeconds(
            context.getTotalPrepareTime()
            );

        log.debug("Total prepare time [{}]",  context.getTotalPrepareTime());
        log.debug("Earliest start time [{}]", earliestStartTime);

        if (schedule != null)
            {
            IvoaRequestedScheduleItem requested = schedule.getRequested();
            if (requested != null)
                {
                String durationString = requested.getDuration();
                if (durationString != null)
                    {
                    try {
                        log.debug("Duration string [{}]", durationString);
                        Duration durationValue = Duration.parse(
                            durationString
                            );
                        log.debug("Duration value [{}]", durationValue);
                        context.setExecutionDuration(
                            durationValue
                            );
                        }
                    catch (Exception ouch)
                        {
                        context.addWarning(
                            "urn:input-syntax-fail",
                            "Unable to parse duration [${string}][${message}]",
                            Map.of(
                                "value",
                                durationString,
                                "message",
                                ouch.getMessage()
                                )
                            );
                        success = false ;
                        context.valid(false);
                        }
                    }

                String startString = requested.getStart();
                if (startString != null)
                    {
                    try {
                        log.debug("Interval String [{}]", startString);
                        Interval startinterval = Interval.parse(
                            startString
                            );

                        log.debug("Interval value [{}]", startinterval);
                        if (startinterval.startsBefore(earliestStartTime))
                            {
                            log.warn("Start interval starts before earliest start time [{}][{}]", startinterval.getStart(), earliestStartTime);
                            // TODO Add a message ..
                            // TODO Fail the request ..
                            }
                        else {
                            context.setStartInterval(
                                startinterval
                                );
                            }
                        }
                    catch (Exception ouch)
                        {
                        log.debug("Exception [{}][{}]", ouch.getMessage(), ouch.getClass());
                        context.addWarning(
                            "urn:input-syntax-fail",
                            "Unable to parse interval [${string}][${message}]",
                            Map.of(
                                "string",
                                startString,
                                "message",
                                ouch.getMessage()
                                )
                            );
                        success = false ;
                        context.valid(false);
                        }
                    }
                }
            }

        if (context.getStartInterval() == null)
            {
            Interval defaultStartInterval = Interval.of(
                earliestStartTime,
                DEFAULT_START_INTERVAL_DURATION
                );
            log.debug("Null start interval, using default [{}]", defaultStartInterval);
            context.setStartInterval(
                defaultStartInterval
                );
            }

        if (context.getExecutionDuration() == null)
            {
            log.debug("Duration is empty, using default [{}]", DEFAULT_SESSION_DURATION);
            context.setExecutionDuration(
                DEFAULT_SESSION_DURATION
                );
            }
        return success ;
        }

    
    @Override
    public OfferSetEntity stageTwo(final Platform platform, final OfferSetEntity offersetEntity, final OfferSetRequestParserContext offersetContext, int offerCount)
        {
        log.debug("stageTwo(Platform , OfferSetEntity, OfferSetRequestParserContext)");
        log.debug("Context valid [{}]", offersetContext.valid());
        
        //
        // Start with NO, and set to YES when we have at least one offer.
        offersetEntity.setResult(
            IvoaOfferSetResponse.ResultEnum.NO
            );
        //
        // Transfer the validation messages to the entity.
        offersetEntity.claimMessages(
            offersetContext.getMessages()
            );
        //
        // If everything is OK.
        log.debug("Context valid [{}]", offersetContext.valid());
        if (offersetContext.valid())
            {
            //
            // Generate some offers ..
            log.debug("---- ---- ---- ----");
            log.debug("Generating offers ....");
            log.debug("Execution start [{}]", offersetContext.getStartInterval());
            log.debug("Execution duration [{}]", offersetContext.getExecutionDuration());

            log.debug("Min cores [{}]",  offersetContext.getTotalMinCores());
            log.debug("Max cores [{}]",  offersetContext.getTotalMaxCores());
            log.debug("Min memory [{}]", offersetContext.getTotalMinMemory());
            log.debug("Max memory [{}]", offersetContext.getTotalMaxMemory());
            log.debug("---- ---- ---- ----");

            //
            // Generate a list of offers for our criteria.
            List<ComputeResourceOffer> computeOffers =  platform.getComputeResourceOfferFactory().generate(
                offersetContext.getStartInterval(),
                offersetContext.getExecutionDuration(),
                offersetContext.getTotalMinCores(),
                offersetContext.getTotalMinMemory(),
                offerCount
                );
            //
            // Create an ExecutionSession for each offer.
            for (ComputeResourceOffer computeOffer : computeOffers)
                {
                log.debug("OfferBlock [{}]", computeOffer.getStartTime());
                // TODO Fix this nasty class cast ....
                // Needed because the platform returns an AbstractExecutionSessionEntityFactory, which creates an AbstractExecutionSessionEntity.
                // To make this work we need to go down the rabbit hole and change all the things that use SimpleExecutionSessionEntity to use AbstractExecutionSessionEntity.
                // TODO Later ...
                SimpleExecutionSessionEntity executionSessionEntity = (SimpleExecutionSessionEntity) platform.getExecutionSessionFactory().create(
                    offersetEntity,
                    offersetContext,
                    computeOffer
                    );
                log.debug("ExecutionEntity [{}]", executionSessionEntity);

                //
                // Build a new ExecutableEntity and add it to our SessionEntity.
                executionSessionEntity.setExecutable(
                    offersetContext.getExecutableResult().build(
                        executionSessionEntity
                        )
                    );

                //
                // Add our compute resources.
                AbstractComputeResourceEntity computeResourceEntity = null;
                for (AbstractComputeResourceValidator.Result result : offersetContext.getComputeValidatorResults())
                    {
                    computeResourceEntity = result.build(
                        executionSessionEntity,
                        computeOffer
                        );
                    }
                //
                // Add our storage resources.
                for (AbstractStorageResourceValidator.Result result : offersetContext.getStorageValidatorResults())
                    {
                    result.build(
                        executionSessionEntity
                        );
                    }
                //
                // Add our data resources.
                for (AbstractDataResourceValidator.Result result : offersetContext.getDataResourceValidatorResults())
                    {
                    result.build(
                        executionSessionEntity
                        );
                    }
                //
                // Add our volume mounts to the compute resource.
                if (computeResourceEntity instanceof SimpleComputeResourceEntity simpleCompute)
                    {
                    for (AbstractVolumeMountValidator.Result result : offersetContext.getVolumeValidatorResults())
                        {
                        result.build(
                            simpleCompute
                            );
                        }
                    }

                //
                // Confirm we have at least one result.
                offersetEntity.setResult(
                    IvoaOfferSetResponse.ResultEnum.YES
                    );
                }
            }

        return offersetEntity;
        }
    }
