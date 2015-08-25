package org.hisp.dhis.mobile.log;

import javax.microedition.lcdui.*;

/**
 * 
 * @author Paul Mark Castillo
 */
public class LogMonitorScreen
    extends Form
    implements CommandListener
{

    /**
     *
     */
    private int count = 0;

    /**
     *
     */
    Ticker filters = new Ticker( "" );

    /**
     *
     */
    Command settingsCommand = new Command( "Settings", Command.OK, 1 );

    /**
     *
     */
    Command saveToFileCommand = new Command( "Save To File", Command.OK, 2 );

    /**
     *
     */
    Command clearCommand = new Command( "Clear", Command.OK, 3 );

    /**
     *
     */
    Command backCommand = new Command( "Exit", Command.EXIT, 4 );

    /**
     *
     */
    public LogMonitorScreen()
    {
        super( "Logs Monitor" );
        setTicker( filters );
        addCommand( settingsCommand );
        if ( LogMan.isEnableSaveToFile() )
        {
            addCommand( saveToFileCommand );
        }
        addCommand( clearCommand );
        addCommand( backCommand );
        setCommandListener( this );
    }

    /**
     *
     */
    public void updateHeader()
    {
        setTitle( "Logs: " + LogUtils.getLevelName( LogMan.getLevel() ) );
        filters.setString( "+[" + LogMan.getIncludeTagsStr() + "]" + "-[" + LogMan.getExcludeTagsStr() + "]" + "+\""
            + LogMan.getIncludeMessagesStr() + "\"" + "-\"" + LogMan.getExcludeMessagesStr() + "\"" );
    }

    /**
     * 
     * @param log
     */
    public void addLog( Log log )
    {
        if ( log != null )
        {
            while ( size() >= LogMan.getBuffSize() )
            {
                delete( 0 );
            }

            String countStr = "#" + ++count + ": ";
            StringItem si = new StringItem( countStr, LogUtils.composeLog( log, true ) );

            append( si );

            if ( LogMan.getDisplay().getCurrent() == this )
            {
                LogMan.getDisplay().setCurrentItem( si );
            }
        }
    }

    /**
     *
     */
    public void clear()
    {
        deleteAll();
        count = 0;
    }

    /**
     *
     */
    public void clearCommand()
    {
        clear();
        LogMan.clearLogs();
    }

    /**
     *
     */
    public void showSettingsCommand()
    {
        LogMan.showSettingsScreen();
    }

    /**
     *
     */
    public void saveToFileCommand()
    {
        if ( LogMan.isEnableSaveToFile() )
        {
            LogMan.saveToFile();
        }
    }

    /**
     *
     */
    public void backCommand()
    {
        LogMan.showPreviousScreen();
    }

    /**
     * 
     * @param c
     * @param d
     */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == clearCommand )
        {
            clearCommand();
        }
        else if ( c == settingsCommand )
        {
            showSettingsCommand();
        }
        else if ( c == saveToFileCommand )
        {
            saveToFileCommand();
        }
        else if ( c == backCommand )
        {
            backCommand();
        }
    }
}
