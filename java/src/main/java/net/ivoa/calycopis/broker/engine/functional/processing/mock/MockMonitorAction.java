package net.ivoa.calycopis.broker.engine.functional.processing.mock;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponent;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecyclePhase;

@Slf4j
public class MockMonitorAction extends MockDelayAction
    {
    public MockMonitorAction(final LifecycleComponent component, int delay)
        {
        super(
            component,
            delay
            );
        }
    
    @Override
    public void preProcess(final LifecycleComponent component)
        {
        log.debug(
            "Pre-processing [{}][{}]",
            component.getUuid(),
            component.getClass().getSimpleName()
            );
        super.preProcess(
            component
            );
        }

    @Override
    public void postProcess(final LifecycleComponent component)
        {
        if (component instanceof MockMonitorableComponent)
            {
            MockMonitorableComponent monitorable = (MockMonitorableComponent) component;
            int count = monitorable.getLifecycleLoopCount();
            count--;

            log.debug(
                "Post-processing [{}][{}] count [{}]",
                component.getUuid(),
                component.getClass().getSimpleName(),
                count
                );

            monitorable.setLifecycleLoopCount(
                count 
                );

            if (count <= 0)
                {
                component.setPhase(
                    IvoaLifecyclePhase.RELEASING
                    );
                }
            }

        else {
            log.error(
                "Unexpected action class [{}] while post-processing [{}]",
                component.getClass().getSimpleName(),
                component.getUuid()
                );
        
            }
        
        super.postProcess(
            component
            );
        }
    }
