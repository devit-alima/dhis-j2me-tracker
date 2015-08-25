package org.hisp.dhis.mobile.log;

/**
 * 
 * @author Paul Mark Castillo
 */
public class ConsoleLogger
    extends Logger
{
    /**
     * 
     */
    private boolean includeTimeDay = true;

    /**
     * 
     */
    private String consolePrefix;

    /**
     * 
     */
    public ConsoleLogger(boolean includeTimeDay, String consolePrefix)
    {
        System.out.println( "[LOG] Opening Console Logger..." );
        setIncludeTimeDay( includeTimeDay );
        setConsolePrefix( consolePrefix );
        setInitialized( true );
    }

    /**
     *
     */
    public void getLogs()
    {
        System.out.println( "[LOG] Console Logger getting logs..." );
    }

    /**
     * 
     * @param log
     */
    public void addLog( Log log )
    {
        if ( log != null )
        {
            String composedLog = LogUtils.composeLog( log, includeTimeDay );
            if (consolePrefix != null) {
                composedLog = consolePrefix + " " + composedLog;
            }
            System.out.println( composedLog );
        }
    }

    /**
     *
     */
    public void clearLogs()
    {
        System.out.println( "[LOG] Console Logger clearing logs..." );
        LogUtils.clearConsole();
    }

    /**
     * 
     */
    public void close()
    {
        System.out.println( "[LOG] Closing Console Logger..." );
    }

    /**
     * 
     * @return
     */
    public boolean isIncludeTimeDay()
    {
        return includeTimeDay;
    }

    /**
     * 
     * @param includeTimeDay
     */
    public void setIncludeTimeDay( boolean includeTimeDay )
    {
        this.includeTimeDay = includeTimeDay;
    }

    /**
     * 
     * @return
     */
    public String getConsolePrefix()
    {
        return consolePrefix;
    }

    /**
     * 
     * @param consolePrefix
     */
    public void setConsolePrefix( String consolePrefix )
    {
        this.consolePrefix = consolePrefix;
    }
}
