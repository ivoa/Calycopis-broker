/**
 * 
 */
package net.ivoa.calycopis.broker.engine.entities.executable.jupyter;

import java.net.URI;

import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutable;

/**
 * 
 */
public interface JupyterNotebook
    extends AbstractExecutable
    {
    /**
     * The OpenAPI type identifier.
     * 
     */
    public static final URI KIND_DISCRIMINATOR = URI.create("https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/executable/jupyter-notebook.yaml") ;
    
    /**
     * Get the location of the notebook.
     *
     */
    public String getLocation();
    
    }
