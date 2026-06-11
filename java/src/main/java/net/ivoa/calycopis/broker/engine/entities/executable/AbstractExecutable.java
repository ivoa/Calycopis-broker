/**
 * 
 */
package net.ivoa.calycopis.broker.engine.entities.executable;

import java.net.URI;

import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponent;

/**
 * 
 */
public interface AbstractExecutable
extends LifecycleComponent
    {
    
    /**
     * The webapp path for executables.
     * 
     */
    public static final URI WEBAPP_PATH = URI.create("executables/"); 
    
    }
