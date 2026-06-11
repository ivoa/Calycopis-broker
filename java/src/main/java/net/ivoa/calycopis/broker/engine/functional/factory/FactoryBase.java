package net.ivoa.calycopis.broker.engine.functional.factory;

import java.util.UUID;

public interface FactoryBase
    {

    /**
     * Get this factory's identifier.
     *
     */
    public UUID getUuid();

    }