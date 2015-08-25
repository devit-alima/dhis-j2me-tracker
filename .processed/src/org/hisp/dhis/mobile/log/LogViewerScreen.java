package org.hisp.dhis.mobile.log;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

/**
 * 
 * @author Paul Mark Castillo
 */
public class LogViewerScreen
    extends Form
    implements CommandListener
{

    /**
     *
     */
    private LogMidlet logMidletRef;

    /**
     *
     */
    private RMSLogger rmsLogger = new RMSLogger();

    /**
     *
     */
    private int index = 1;

    /**
     *
     */
    private StringItem recordID = new StringItem( "Record ID: ", "" );

    /**
     *
     */
    private StringItem time = new StringItem( "Time: ", "" );

    /**
     *
     */
    private StringItem level = new StringItem( "Level: ", "" );

    /**
     *
     */
    private StringItem tags = new StringItem( "Tags: ", "" );

    /**
     *
     */
    private StringItem message = new StringItem( "Message: ", "" );

    /**
     *
     */
    private Command nextCommand = new Command( "Next", Command.OK, 0 );

    /**
     *
     */
    private Command previousCommand = new Command( "Previous", Command.OK, 1 );

    /**
     *
     */
    private Command exitCommand = new Command( "Exit", Command.EXIT, 2 );

    /**
     * 
     * @param logMidletRef
     */
    public LogViewerScreen( LogMidlet logMidletRef )
    {
        super( "Log Viewer" );
        this.logMidletRef = logMidletRef;
        rmsLogger.openRecordDB();

        append( recordID );
        append( time );
        append( level );
        append( tags );
        append( message );

        addCommand( nextCommand );
        addCommand( previousCommand );
        addCommand( exitCommand );
        setCommandListener( this );

        refresh();
    }

    /**
     * 
     * @param c
     * @param d
     */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == nextCommand )
        {
            next();
        }
        else if ( c == previousCommand )
        {
            previous();
        }
        else if ( c == exitCommand )
        {
            exit();
        }
    }

    /**
     *
     */
    public void refresh()
    {
        recordID.setText( index + "" );
        try
        {
            Log log = rmsLogger.getLog( index );
            if ( log != null )
            {

                String timeStr = log.getTimeDay();
                if ( timeStr != null )
                {
                    time.setText( timeStr );
                }
                else
                {
                    time.setText( "null" );
                }

                String levelStr = LogUtils.getLevelName( log.getLevel() );
                if ( levelStr != null )
                {
                    level.setText( levelStr );
                }
                else
                {
                    level.setText( "null" );
                }

                String tagsStr = "[" + LogUtils.toStringTags( log.getTags(), "|" ) + "]";
                if ( tagsStr != null )
                {
                    tags.setText( tagsStr );
                }

                String messageStr = log.getMessage();
                if ( messageStr != null )
                {
                    message.setText( messageStr );
                }
                else
                {
                    message.setText( "null" );
                }
            }
            else
            {
                displayError( "null" );
            }
        }
        catch ( Exception e )
        {
            displayError( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param str
     */
    private void displayError( String str )
    {
        time.setText( str );
        level.setText( str );
        tags.setText( str );
        message.setText( str );
    }

    /**
     *
     */
    public void next()
    {
        index++;
        refresh();
    }

    /**
     *
     */
    public void previous()
    {
        if ( index > 1 )
        {
            index--;
            refresh();
        }
    }

    /**
     *
     */
    public void exit()
    {
        rmsLogger.closeRecordDB();
        logMidletRef.showMainMenu();
    }
}
