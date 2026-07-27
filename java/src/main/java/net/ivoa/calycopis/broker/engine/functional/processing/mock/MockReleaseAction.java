package net.ivoa.calycopis.broker.engine.functional.processing.mock;

import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.openapi.spring.model.IvoaLifecyclePhase;

public class MockReleaseAction extends MockDelayAction
    {

    public MockReleaseAction(final LifecycleComponentEntity component, int delay)
        {
        super(
            component,
            IvoaLifecyclePhase.RELEASING,
            IvoaLifecyclePhase.COMPLETED,
            delay
            );
        }
    }
