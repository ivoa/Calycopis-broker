package net.ivoa.calycopis.functional.processing.mock;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.engine.lifecycle.LifecyclePhase;

public class MockPrepareAction extends MockDelayAction
    {

    public MockPrepareAction(final LifecycleComponentEntity component, int delay)
        {
        super(
            component,
            LifecyclePhase.PREPARING,
            LifecyclePhase.AVAILABLE,
            delay
            );
        }
    }
