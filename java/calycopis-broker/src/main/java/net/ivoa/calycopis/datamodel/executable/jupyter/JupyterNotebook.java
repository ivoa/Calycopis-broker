/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable.jupyter;

import java.net.URI;

import net.ivoa.calycopis.datamodel.executable.AbstractExecutable;

/**
 * 
 */
public interface JupyterNotebook
    extends AbstractExecutable
    {
    /**
     * The type discriminator for Jupyter notebooks.
     * 
     */
    public static final URI TYPE_DISCRIMINATOR = URI.create("https://www.purl.org/ivoa.net/EB/schema/v1.0/types/executable/jupyter-notebook-1.0") ;
    
    /**
     * Get the location of the notebook.
     *
     */
    public String getLocation();
    
    }
