package org.hisp.dhis.mobile.log;


/**
 * 
 * @author Paul Mark Castillo
 */
public abstract class Logger
{
    /**
     * 
     */
    private boolean initialized = false;

    /**
     * 
     * @return
     */
    public abstract void getLogs();

    /**
     * 
     * @param log
     */
    public abstract void addLog( Log log );

    /**
     * 
     */
    public abstract void clearLogs();

    /**
     * 
     */
    public abstract void close();

    /**
     * @return the initialized
     */
    public boolean isInitialized()
    {
        return initialized;
    }

    /**
     * @param initialized the initialized to set
     */
    public void setInitialized( boolean initialized )
    {
        this.initialized = initialized;
    }
}
