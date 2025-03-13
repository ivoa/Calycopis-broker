/**
 * 
 */
package net.ivoa.calycopis.offerset;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.compute.simple.SimpleComputeResource;
import net.ivoa.calycopis.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.execution.ExecutionSessionEntityFactory;
import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.offers.OfferBlock;
import net.ivoa.calycopis.offers.OfferBlockFactory;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResourceList;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequestSchedule;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse;
import net.ivoa.calycopis.openapi.model.IvoaScheduleRequestBlock;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;
import net.ivoa.calycopis.storage.AbstractStorageResourceValidatorFactory;

/**
 * 
 */
@Slf4j
@Component
public class OfferSetRequestParserImpl
    extends FactoryBaseImpl
    implements OfferSetRequestParser
    {
    /**
     * Our schedule block factory.
     * 
     */
    private final OfferBlockFactory offerBlockFactory;

    // TODO Replace this with a builder ..
    private final ExecutionSessionEntityFactory executionSessionFactory;

    /**
     * Executable Validators.
     * 
     */
    private final AbstractExecutableValidatorFactory executableValidators;

    /**
     * Storage resource Validators.
     * 
     */
    private final AbstractStorageResourceValidatorFactory storageValidators;

    /**
     * Data resource Validators.
     * 
     */
    private final AbstractDataResourceValidatorFactory dataValidators;

    /**
     * Compute resource Validators.
     * 
     */
    private final AbstractComputeResourceValidatorFactory computeValidators;

    @Autowired
    public OfferSetRequestParserImpl(
        final OfferBlockFactory offerBlockFactory, 
        final AbstractExecutableValidatorFactory executableValidators,
        final AbstractStorageResourceValidatorFactory storageValidators, 
        final AbstractDataResourceValidatorFactory dataValidators, 
        final AbstractComputeResourceValidatorFactory computeValidators,
        final ExecutionSessionEntityFactory executionSessionFactory
        ){
        super();
        this.offerBlockFactory    = offerBlockFactory ;
        this.executableValidators = executableValidators ;
        this.storageValidators    = storageValidators ;
        this.dataValidators       = dataValidators ;
        this.computeValidators    = computeValidators ;
        this.executionSessionFactory = executionSessionFactory;
        }
    
    @Override
    public void process(final IvoaOfferSetRequest offersetRequest, final OfferSetEntity offersetEntity)
        {
        log.debug("process(IvoaOfferSetRequest, OfferSetEntity)");
        OfferSetRequestParserContext state = new OfferSetRequestParserContextImpl(
            this,
            offersetRequest,
            offersetEntity
            );
        //
        // Validate the request.
        validate(
            state
            );
        // Exit if something was rejected.
        
        //
        // Construct the offers.
        
        
        }
    
    /**
     * Validate the request components.
     * 
     */
    public void validate(final OfferSetRequestParserContext context)
        {
        log.debug("validate(OfferSetRequestParserState)");
        final IvoaOfferSetRequest offersetRequest = context.getOriginalOfferSetRequest();
        final IvoaOfferSetRequest offersetResult  = context.getValidatedOfferSetRequest();
        //
        // Initialise our result. 
        if (offersetResult.getResources() == null)
            {
            offersetResult.setResources(
                new IvoaExecutionResourceList()
                );
            }
        //
        // Validate the requested resources.
        log.debug("Validating the requested resources");
        if (offersetRequest.getResources() != null)
            {
            //
            // Validate the requested storage resources.
            log.debug("Validating the requested storage resources");
            if (offersetRequest.getResources().getStorage() != null)
                {
                for (IvoaAbstractStorageResource resource : offersetRequest.getResources().getStorage())
                    {
                    storageValidators.validate(
                        resource,
                        context
                        );
                    // TODO Check the result ?
                    }
                }
            //
            // Validate the requested data resources.
            log.debug("Validating the requested data resources");
            if (offersetRequest.getResources().getData() != null)
                {
                for (IvoaAbstractDataResource resource : offersetRequest.getResources().getData())
                    {
                    dataValidators.validate(
                        resource,
                        context
                        );            
                    // TODO Check the result ?
                    }
                }
            //
            // Validate the requested compute resources.
            log.debug("Validating the requested compute resources");
            if (offersetRequest.getResources().getCompute() != null)
                {
                for (IvoaAbstractComputeResource resource : offersetRequest.getResources().getCompute())
                    {
                    computeValidators.validate(
                        resource,
                        context
                        );            
                    // TODO Check the result ?
                    }
                }
            }
        log.debug("Finished validating the resources");
        
        // Exit if errors ..
        
        //
        // If we haven't found a compute resource, add a default.
        log.debug("Checking for empty compute resource list");
        if (context.getComputeValidatorResults().isEmpty())
            {
            log.debug("Adding a default compute resource");
            IvoaSimpleComputeResource compute = new IvoaSimpleComputeResource(
                SimpleComputeResource.TYPE_DISCRIMINATOR 
                );
            computeValidators.validate(
                compute,
                context
                );
            // TODO Check the result ?
            }

        // Exit if errors ..

        //
        // Validate the requested executable.
        log.debug("Validating the requested executable");
        if (offersetRequest.getExecutable() != null)
            {
            executableValidators.validate(
                offersetRequest.getExecutable(),
                context
                );
            // TODO Check the result ?
            }
        else {
            log.error("Offerset request has no executable");
            context.getOfferSetEntity().addWarning(
                "urn:executable-required",
                "Description of the executable is required"
                );
            context.valid(false);
            }
        
        //
        // Validate the schedule.
        validate(
            offersetRequest.getSchedule(),
            context
            );
        
        //
        // This is specific to the CANMFAR platforms.
        // Fail if we have found too many compute resources,
        // Other platforms may be able to support more than one compute resources.
        if (context.getComputeValidatorResults().size() > 1)
            {
            log.warn("Found more than one compute resources");
            context.getOfferSetEntity().addWarning(
                "urn:not-supported-message",
                "Multiple compute resources not supported"
                );
            context.valid(false);
            }
        
        // Exit if errors ..
        
        build(context);
        }

    
//
// TOD Move this part to a separate schedule validator.
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
    public static final Duration DEFAULT_START_DURATION = Duration.ofHours(2);
    
    /**
     * Validate the requested Schedule.
     *
     */
    public void validate(final IvoaOfferSetRequestSchedule schedule, final OfferSetRequestParserContext state)
        {
        log.debug("validate(IvoaExecutionSessionRequestSchedule)");
        if (schedule != null)
            {
            IvoaScheduleRequestBlock requested = schedule.getRequested();
            if (requested != null)
                {
                String durationstr = requested.getDuration();
                if (durationstr != null)
                    {
                    try {
                        Duration durationval = Duration.parse(
                            durationstr
                            );
                        log.debug("Duration [{}][{}]", durationstr, durationval);
                        state.setExecutionDuration(
                            durationval
                            );
                        }
                    catch (Exception ouch)
                        {
                        state.getOfferSetEntity().addWarning(
                            "urn:input-syntax-fail",
                            "Unable to parse duration [${string}][${message}]",
                            Map.of(
                                "value",
                                durationstr,
                                "message",
                                ouch.getMessage()
                                )
                            );
                        state.valid(false);
                        }
                    }

                List<String> startstrlist = requested.getStart();
                if (startstrlist != null)
                    {
                    for (String startstr : startstrlist)
                        {
                        try {
                            Interval startint = Interval.parse(
                                startstr
                                );
                            log.debug("Interval [{}][{}]", startstr, startint);
                            state.addStartInterval(
                                startint
                                );
                            }
                        catch (Exception ouch)
                            {
                            state.getOfferSetEntity().addWarning(
                                "urn:input-syntax-fail",
                                "Unable to parse interval [${string}][${message}]",
                                Map.of(
                                    "value",
                                    startstr,
                                    "message",
                                    ouch.getMessage()
                                    )
                                );
                            state.valid(false);
                            }
                        }
                    }
                }
            }

        if (state.getStartIntervals().isEmpty())
            {
            Interval defaultint = Interval.of(
                Instant.now(),
                DEFAULT_START_DURATION
                );
            log.debug("Interval list is empty, adding default [{}]", defaultint);
            state.addStartInterval(
                defaultint
                );
            }

        if (state.getExecutionDuration() == null)
            {
            log.debug("Duration is empty, using default [{}]", DEFAULT_SESSION_DURATION);
            state.setExecutionDuration(
                DEFAULT_SESSION_DURATION
                );
            }
        }
    
    /**
     * Build the entities from the validated input.
     *  
     */
    public void build(final OfferSetRequestParserContext state)
        {
        log.debug("build(final OfferSetRequestParserState)");

        //
        // Start with NO, and set to YES when we have at least one offer.
        IvoaOfferSetResponse.ResultEnum resultEnum = IvoaOfferSetResponse.ResultEnum.NO;

        //
        // If everything is OK.
        if (state.valid())
            {
            //
            // Generate some offers ..
            log.debug("---- ---- ---- ----");
            log.debug("Generating offers ....");
            log.debug("Start intervals [{}]", state.getStartIntervals());
            log.debug("Execution duration [{}]", state.getExecutionDuration());
            
            log.debug("Min cores [{}]",  state.getTotalMinCores());
            log.debug("Max cores [{}]",  state.getTotalMaxCores());
            log.debug("Min memory [{}]", state.getTotalMinMemory());
            log.debug("Max memory [{}]", state.getTotalMaxMemory());
            log.debug("---- ---- ---- ----");

            //
            // Populate our OfferSet ..
            for (Interval startInterval : state.getStartIntervals())
                {
                //
                // Generate a list of available blocks.
                List<OfferBlock> offerblocks = offerBlockFactory.generate(
                    startInterval,
                    state.getExecutionDuration(),
                    state.getTotalMinCores(),
                    state.getTotalMinMemory()
                    );
                //
                // Create an ExecutionSession for each block. 
                for (OfferBlock offerblock : offerblocks)
                    {
                    log.debug("OfferBlock [{}]", offerblock.getStartTime());
                    ExecutionSessionEntity executionSessionEntity = executionSessionFactory.create(
                        state.getOfferSetEntity(),
                        state,
                        offerblock
                        );
                    log.debug("ExecutionEntity [{}]", executionSessionEntity);

                    log.debug("Executable [{}]", state.getExecutableResult());

                    //
                    // Build a new ExecutableEntity and add it to our ExecutionSessionEntity.
                    executionSessionEntity.setExecutable(
                        state.getExecutableResult().getBuilder().build(
                            executionSessionEntity
                            )
                        );
                    
                    //
                    // Build and add our first compute resource.
                    List<AbstractComputeResourceValidator.Result> computeValidatorResults = state.getComputeValidatorResults();                    
                    AbstractComputeResourceValidator.Result computeValidatorResult = computeValidatorResults.getFirst();
                    if (computeValidatorResult != null)
                        {
                        AbstractComputeResourceEntity computeResourceEntity = computeValidatorResult.getBuilder().build(
                            executionSessionEntity,
                            offerblock
                            );
                        executionSessionEntity.addComputeResource(
                            computeResourceEntity
                            );
                        }

/*
 * 
                    //
                    // Build and add our storage resources.
                    List<ComputeResourceValidator.Result> computeValidatorResults = state.getComputeValidatorResults();                    
                    for (ComputeResourceValidator.Result computeValidatorResult : computeValidatorResults)
                        {
                        AbstractComputeResourceEntity computeResourceEntity = computeValidatorResult.getBuilder().build(
                            executionSessionEntity
                            );
                        executionSessionEntity.addComputeResource(
                            computeResourceEntity
                            );
                        }
                    
                    //
                    // Build and add our data resources.
                    List<ComputeResourceValidator.Result> computeValidatorResults = state.getComputeValidatorResults();                    
                    for (ComputeResourceValidator.Result computeValidatorResult : computeValidatorResults)
                        {
                        AbstractComputeResourceEntity computeResourceEntity = computeValidatorResult.getBuilder().build(
                            executionSessionEntity
                            );
                        executionSessionEntity.addComputeResource(
                            computeResourceEntity
                            );
                        }
 *                     
 */
                    
                    //
                    // Add the ExecutionSession to the OfferSet.
                    state.getOfferSetEntity().addExecutionSession(
                        executionSessionEntity
                        );
                    
                    
                    /*
                     * 
                    log.debug("Executable [{}][{}]", state.getExecutable().getObject().getName(), state.getExecutable().getObject().getClass().getName());
                    
                    for (DataResourceValidator.Result dataResult : state.getDataResourceValidatorResults())
                        {
                        log.debug("Data result [{}]", dataResult);
                        }
                    
                    for (ComputeResourceValidator.Result computeResult : state.getComputeValidatorResults())
                        {
                        log.debug("Compute result [{}]", computeResult);
                        }
                     * 
                     */
                    
                    //
                    // Confirm we have at least one result.
                    resultEnum = IvoaOfferSetResponse.ResultEnum.YES;
                    }
                }
            }
        //
        // Set the OfferSet result.
        state.getOfferSetEntity().setResult(
            resultEnum
            );
        }
    }
