package net.ivoa.calycopis.functional.factory;

/**
 * Broker-layer factory base implementation.
 *
 * Extends the engine's {@link net.ivoa.calycopis.engine.factory.FactoryBaseImpl}
 * so that all broker factory implementations automatically satisfy the engine
 * {@code FactoryBase} contract.  Also implements the broker's (deprecated)
 * {@link FactoryBase} for backward compatibility with broker code that still
 * references the broker-layer interface.
 */
public class FactoryBaseImpl
extends net.ivoa.calycopis.engine.factory.FactoryBaseImpl
implements FactoryBase
    {

    /**
     * Protected constructor, initialises the factory UUID.
     */
    public FactoryBaseImpl()
        {
        super();
        }

    }
