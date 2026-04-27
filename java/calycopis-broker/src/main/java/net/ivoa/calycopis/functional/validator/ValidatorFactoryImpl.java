/*
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-02-17T07:10:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 8,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-17T13:20:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 10,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.functional.validator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;

/**
 * Base class for validator factories.
 * 
 */
public abstract class ValidatorFactoryImpl<ObjectType, EntityType extends ComponentEntity>
    extends FactoryBaseImpl
    implements ValidatorFactory<ObjectType, EntityType>
    {

    /**
     * Our list of validators.
     * 
     */
    protected List<Validator<ObjectType, EntityType>> validators = new ArrayList<Validator<ObjectType, EntityType>>();

    /**
     * Add a validator to our list.
     * 
     */
    public void addValidator(
        final Validator<ObjectType, EntityType> validator
        ){
        this.validators.add(validator);
        }
    
    /**
     * Report an unknown type.
     *
     */
    public abstract void unknown(
        final OfferSetRequestParserContext context,
        final ObjectType object
        );

    @Override
    public ResultEnum validate(
        final ObjectType requested,
        final OfferSetRequestParserContext context
        ){
        //
        // Try each of the validators in our list.
        // Validators are tried in registration order and the first one to
        // return ACCEPTED or FAILED wins. Because of this, validators that
        // handle more specific subtypes (e.g. SkaoDataResource) must be
        // registered before validators that handle their parent types
        // (e.g. IvoaDataResource).
        for (Validator<ObjectType, EntityType> validator : validators)
            {
            ResultEnum result = validator.validate(
                requested,
                context
                );
            switch(result)
                {
                case CONTINUE:
                    break;
                case ACCEPTED:
                case FAILED:
                    return result;
                }
            }
        //
        // Fail the validation if we didn't find a matching validator.
        unknown(
            context,
            requested
            );
        return ResultEnum.FAILED;
        }

    /**
     * Report an unknown resource.
     *
     */
    protected void unknown(
        final OfferSetRequestParserContext context,
        final URI kind,
        final IvoaComponentMetadata meta
        ){
        if (meta != null)
            {
            context.addWarning(
                "uri:unknown-type",
                "Unknown kind [${kind}][${name}][${uuid}]",
                Map.of(
                    "kind",
                    (kind != null) ? kind.toString() : "null",
                    "name",
                    (meta.getName() != null) ? meta.getName() : "null",
                    "uuid",
                    (meta.getUuid() != null) ? meta.getUuid().toString() : "null"
                    )
                );
            }
        else
            {
            context.addWarning(
                "uri:unknown-type",
                "Unknown kind [${kind}]",
                Map.of(
                    "kind",
                    kind.toString()
                    )
                );
            }
        context.valid(false);
        }
    }
