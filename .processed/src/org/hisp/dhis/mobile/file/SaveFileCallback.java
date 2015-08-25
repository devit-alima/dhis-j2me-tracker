package org.hisp.dhis.mobile.file;

import java.io.OutputStream;

/**
 * 
 * @author Paul Mark Castillo
 */
public interface SaveFileCallback
{
    /**
     * 
     * @param os
     */
    public void saveFile( OutputStream os );
}
