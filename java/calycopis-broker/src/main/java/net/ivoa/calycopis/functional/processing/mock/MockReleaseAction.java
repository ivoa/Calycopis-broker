package net.ivoa.calycopis.functional.processing.mock;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.engine.lifecycle.LifecyclePhase;

public class MockReleaseAction extends MockDelayAction
    {

    public MockReleaseAction(final LifecycleComponentEntity component, int delay)
        {
        super(
            component,
            LifecyclePhase.RELEASING,
            LifecyclePhase.COMPLETED,
            delay
            );
        }
    }
