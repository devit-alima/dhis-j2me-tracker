package org.hisp.dhis.mobile.log;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import org.hisp.dhis.mobile.file.FileManager;

/**
 * 
 * @author Paul Mark Castillo
 */
public class LogMan
{

    /**
     *
     */
    private static MIDlet midlet;

    /**
     *
     */
    public static final int DISABLED = -1;

    /**
     *
     */
    public static final int DEBUG = 0;

    /**
     *
     */
    public static final int INFO = 1;

    /**
     *
     */
    public static final int DEV = 2;

    /**
     *
     */
    public static final int WARN = 3;

    /**
     *
     */
    public static final int ERROR = 4;

    /**
     *
     */
    public static final int FATAL = 5;

    /**
     * 
     */
    public static final byte LEVEL = DISABLED;

    /**
     *
     */
    private static final int DO_SEND_LOG = 0;

    /**
     *
     */
    private static final int DO_CLEAR_LOG = 1;

    /**
     *
     */
    private static final int DO_GET_LOG = 2;

    /**
     *
     */
    private static final int DO_CLOSE_LOG = 3;

    /**
     *
     */
    private static boolean initialized = false;

    /**
     *
     */
    private static boolean enabled = true;

    /**
     *
     */
    private static boolean enableSaveToFile = true;

    /**
     *
     */
    private static Vector loggers = new Vector();

    /**
     *
     */
    private static int buffSize = 10;

    /**
     *
     */
    private static int level = DEBUG;

    /**
     *
     */
    private static Vector includeTags = new Vector();

    /**
     *
     */
    private static Vector excludeTags = new Vector();

    /**
     *
     */
    private static Vector includeMessages = new Vector();

    /**
     *
     */
    private static Vector excludeMessages = new Vector();

    /**
     *
     */
    private static Vector previousScreens = new Vector();

    /**
     *
     */
    private static LogMonitorScreen logScreen = new LogMonitorScreen();

    /**
     *
     */
    private static LogSettingsScreen logSettingsScreen = new LogSettingsScreen();

    /**
     *
     */
    private static ConsoleLogger cLog;

    /**
     *
     */
    private static RMSLogger rLog;

    /**
     *
     */
    public static void initialize( MIDlet midlet )
    {
        if ( !isInitialized() )
        {
            System.out.println( "[LOG] Initializing Log Management..." );
            setMidlet( midlet );

            cLog = new ConsoleLogger( false, "[DEBUG]" );
            addLogger( cLog );

            rLog = new RMSLogger();
            addLogger( rLog );
        }
    }

    /**
     *
     */
    public static void deInitialize()
    {
        System.out.println( "[LOG] Deinitializing Log Management..." );
        doLogs( DO_CLOSE_LOG, null );
    }

    /**
     * 
     * @param operation
     * @param log
     */
    public static void doLogs( int operation, Log log )
    {
        Enumeration enu = loggers.elements();
        while ( enu.hasMoreElements() )
        {
            Logger logger = (Logger) enu.nextElement();
            if ( logger.isInitialized() )
            {
                switch ( operation )
                {
                case DO_SEND_LOG:
                    logger.addLog( log );
                    break;
                case DO_CLEAR_LOG:
                    logger.clearLogs();
                    break;
                case DO_GET_LOG:
                    logger.getLogs();
                    break;
                case DO_CLOSE_LOG:
                    logger.close();
                    break;
                }
            }
        }
    }

    /**
     * 
     * @param logger
     */
    public static void addLogger( Logger logger )
    {
        if ( logger != null )
        {
            loggers.addElement( logger );
            logger.getLogs();
        }
    }

    /**
     * 
     * @param log
     */
    public static void displayLogs( Log log, boolean reverseOrder )
    {
        logScreen.addLog( log );
    }

    /**
     *
     */
    public static void saveToFile()
    {
        if ( enableSaveToFile )
        {
            FileManager fm = new FileManager( getMidlet() );
            fm.saveFiles( "logs.log", rLog );
        }
    }

    /**
     * 
     * @param filters
     */
    public static void setIncludeTags( String tag )
    {
        System.out.println( "[LOG] Setting include tag: " + tag );
        if ( tag == null )
        {
            tag = "";
        }
        setIncludeTags( LogUtils.toVector( tag ) );
    }

    /**
     * 
     * @return
     */
    public static String getIncludeTagsStr()
    {
        return LogUtils.toStringTags( includeTags, "," );
    }

    /**
     * 
     * @return
     */
    public static String getExcludeTagsStr()
    {
        return LogUtils.toStringTags( excludeTags, "," );
    }

    /**
     * 
     * @param tag
     */
    public static void setExcludeTags( String tag )
    {
        System.out.println( "[LOG] Setting exclude tag: " + tag );
        if ( tag == null )
        {
            tag = "";
        }
        setExcludeTags( LogUtils.toVector( tag ) );
    }

    /**
     * 
     * @return
     */
    public static String getIncludeMessagesStr()
    {
        return LogUtils.toStringTags( includeMessages, "," );
    }

    /**
     * 
     * @param message
     */
    public static void setIncludeMessages( String message )
    {
        System.out.println( "[LOG] Setting include message: " + message );
        if ( message == null )
        {
            message = "";
        }
        setIncludeMessages( LogUtils.toVector( message ) );
    }

    /**
     * 
     * @return
     */
    public static String getExcludeMessagesStr()
    {
        return LogUtils.toStringTags( excludeMessages, "," );
    }

    /**
     * 
     * @param message
     */
    public static void setExcludeMessages( String message )
    {
        System.out.println( "[LOG] Setting exclude message: " + message );
        if ( message == null )
        {
            message = "";
        }
        setExcludeMessages( LogUtils.toVector( message ) );
    }

    /**
     * 
     * @param message
     */
    public static void log( String message )
    {
        log( DEV, message );
    }

    /**
     * 
     * @param e
     */
    public static void log( String tags, Exception e) {
        log ( ERROR, tags + ",Exception", e.getClass().getName() + ": " + e.getMessage());
    }
    
    /**
     * 
     * @param level
     * @param message
     */
    public static void log( int level, String message )
    {
        log( level, "", message );
    }

    /**
     * 
     * @param level
     * @param tags
     * @param message
     */
    public static void log( int aLevel, String tags, String message )
    {
        if ( isEnabled() )
        {
            if ( level <= aLevel )
            {
                log( aLevel, LogUtils.toVector( tags ), message );
            }
        }
    }

    /**
     * 
     * @param level
     * @param tags
     * @param message
     */
    public static void log( int level, Vector tags, String message )
    {
        Log log = new Log();
        log.setLevel( level );
        log.setTags( tags );
        log.setMessage( message );
        log( log );
    }

    /**
     * 
     * @param log
     */
    public static void log( Log log )
    {
        if ( isValidLog( log ) )
        {
            logScreen.addLog( log );
            doLogs( DO_SEND_LOG, log );
        }
    }

    /**
     * 
     * @param log
     * @return
     */
    public static boolean isValidLog( Log log )
    {
        if ( isEnabled() )
        {
            if ( level <= log.getLevel() )
            {
                return isValidContents( log );
            }
        }
        return false;
    }

    /**
     * 
     * @param log
     * @return
     */
    public static boolean isValidContents( Log log )
    {
        Vector logTags = log.getTags();
        String logMessage = log.getMessage();

        if ( LogUtils.isValidIncludeTags( logTags ) && LogUtils.isValidExcludeTags( logTags )
            && LogUtils.isValidIncludeMessage( logMessage ) && LogUtils.isValidExcludeMessage( logMessage ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     *
     */
    public static void showLogMonitorScreen()
    {
        showNextScreen( logScreen );
    }

    /**
     * 
     * @return
     */
    public static Displayable getLogMonitorScreen()
    {
        return logScreen;
    }

    /**
     *
     */
    public static void showSettingsScreen()
    {
        logSettingsScreen.refreshSettings();
        showNextScreen( logSettingsScreen );
    }

    /**
     * 
     * @param displayable
     */
    public static void showNextScreen( Displayable displayable )
    {
        previousScreens.addElement( Display.getDisplay( midlet ).getCurrent() );
        Display.getDisplay( midlet ).setCurrent( displayable );
    }

    /**
     *
     */
    public static void showPreviousScreen()
    {
        Display.getDisplay( midlet ).setCurrent( (Displayable) previousScreens.lastElement() );
        previousScreens.removeElement( previousScreens.lastElement() );
    }

    /**
     *
     */
    public static void clearLogs()
    {
        System.out.println( "[LOG] Clearing logs..." );
        doLogs( DO_CLEAR_LOG, null );
    }

    /**
     *
     */
    public static void refreshLogs()
    {
        System.out.println( "[LOG] Refreshing logs..." );
        logScreen.clear();
        doLogs( DO_GET_LOG, null );
    }

    /**
     * 
     * @return
     */
    public static Display getDisplay()
    {
        return Display.getDisplay( midlet );
    }

    // <editor-fold defaultstate="collapsed" desc="GETTER/SETTER METHODS">
    /**
     * @return the enabled
     */
    public static boolean isEnabled()
    {
        return enabled;
    }

    /**
     * @param aEnabled the enabled to set
     */
    public static void setEnabled( boolean aEnabled )
    {
        System.out.println( "[LOG] Setting enabled: " + aEnabled );
        enabled = aEnabled;
    }

    /**
     * @return the loggers
     */
    public static Vector getLoggers()
    {
        return loggers;
    }

    /**
     * @param aLoggers the loggers to set
     */
    public static void setLoggers( Vector aLoggers )
    {
        loggers = aLoggers;
    }

    /**
     * @return the buffSize
     */
    public static int getBuffSize()
    {
        return buffSize;
    }

    /**
     * @param aBuffSize the buffSize to set
     */
    public static void setBuffSize( int aBuffSize )
    {
        System.out.println( "[LOG] Setting buffer size: " + aBuffSize );
        if ( aBuffSize > 0 )
        {
            buffSize = aBuffSize;
        }
    }

    /**
     * @return the level
     */
    public static int getLevel()
    {
        return level;
    }

    /**
     * @param aLevel the level to set
     */
    public static void setLevel( int aLevel )
    {
        System.out.println( "[LOG] Setting level: " + LogUtils.getLevelName( aLevel ) );
        if ( aLevel == DISABLED )
        {
            setEnabled( false );
        }
        else
        {
            setEnabled( true );
        }
        level = aLevel;
        logScreen.updateHeader();
    }

    /**
     * @return the filters
     */
    public static Vector getIncludeTags()
    {
        return includeTags;
    }

    /**
     * @param aIncludeTags the filters to set
     */
    public static void setIncludeTags( Vector aIncludeTags )
    {
        includeTags = aIncludeTags;
        logScreen.updateHeader();
    }

    /**
     * @return the excludeTags
     */
    public static Vector getExcludeTags()
    {
        return excludeTags;
    }

    /**
     * @param aExcludeTags the excludeTags to set
     */
    public static void setExcludeTags( Vector aExcludeTags )
    {
        excludeTags = aExcludeTags;
        logScreen.updateHeader();
    }

    /**
     * @return the includeMessages
     */
    public static Vector getIncludeMessages()
    {
        return includeMessages;
    }

    /**
     * @param aIncludeMessages the includeMessages to set
     */
    public static void setIncludeMessages( Vector aIncludeMessages )
    {
        includeMessages = aIncludeMessages;
        logScreen.updateHeader();
    }

    /**
     * @return the excludeMessages
     */
    public static Vector getExcludeMessages()
    {
        return excludeMessages;
    }

    /**
     * @param aExcludeMessages the excludeMessages to set
     */
    public static void setExcludeMessages( Vector aExcludeMessages )
    {
        excludeMessages = aExcludeMessages;
        logScreen.updateHeader();
    }

    /**
     * @return the initialized
     */
    public static boolean isInitialized()
    {
        return initialized;
    }

    /**
     * @param aInitialized the initialized to set
     */
    public static void setInitialized( boolean aInitialized )
    {
        initialized = aInitialized;
    }

    /**
     * @return the enableSaveToFile
     */
    public static boolean isEnableSaveToFile()
    {
        return enableSaveToFile;
    }

    /**
     * @param aEnableSaveToFile the enableSaveToFile to set
     */
    public static void setEnableSaveToFile( boolean aEnableSaveToFile )
    {
        enableSaveToFile = aEnableSaveToFile;
    }

    /**
     * @return the midlet
     */
    public static MIDlet getMidlet()
    {
        return midlet;
    }

    /**
     * @param midlet the midlet to set
     */
    public static void setMidlet( MIDlet aMidlet )
    {
        midlet = aMidlet;
    }
    // </editor-fold>
}
