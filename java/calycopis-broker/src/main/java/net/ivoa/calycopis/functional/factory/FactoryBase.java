package net.ivoa.calycopis.functional.factory;

/**
 * Broker-layer factory base interface.
 *
 * @deprecated Use {@link net.ivoa.calycopis.engine.factory.FactoryBase} directly.
 *             This interface now extends the engine version so that all broker
 *             classes implementing it are also valid engine {@code FactoryBase}
 *             instances.
 */
@Deprecated
public interface FactoryBase
extends net.ivoa.calycopis.engine.factory.FactoryBase
    {

    }
