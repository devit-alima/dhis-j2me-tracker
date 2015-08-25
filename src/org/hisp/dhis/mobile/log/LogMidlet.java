package org.hisp.dhis.mobile.log;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

/**
 * @author Paul Mark Castillo
 */
public class LogMidlet
    extends MIDlet
    implements CommandListener
{

    LogMidlet ref = this;

    Display dispRef = Display.getDisplay( this );

    Form frm = new Form( "Log Midlet Main Menu" );

    Command logMonitorCommand = new Command( "Open Log Monitor", Command.OK, 0 );

    Command logViewerCommand = new Command( "Open Log Viewer", Command.OK, 1 );

    Command exitCommand = new Command( "Exit", Command.EXIT, 2 );

    /**
     *
     */
    public LogMidlet()
    {
        frm.append( "PAUL'S LOGGING API V1.0" );
        frm.append( "For the full-featured log monitoring, open the log monitor. It may be resource-intensive, depending on the size of the logs." );
        frm.append( "For the lite-version, open the log view. It may lack features, but it is capable enough to view all the logs regardless of the size." );
        frm.addCommand( logMonitorCommand );
        frm.addCommand( logViewerCommand );
        frm.addCommand( exitCommand );
        frm.setCommandListener( this );
    }

    /**
     *
     */
    public void startApp()
    {
        showMainMenu();
    }

    /**
     *
     */
    public void pauseApp()
    {
    }

    /**
     * 
     * @param unconditional
     */
    public void destroyApp( boolean unconditional )
    {
        if ( LogMan.isInitialized() )
        {
            LogMan.deInitialize();
        }
    }

    /**
     * 
     * @param c
     * @param d
     */
    public void commandAction( final Command c, Displayable d )
    {
        new Thread()
        {

            public void run()
            {
                if ( c == logMonitorCommand )
                {
                    if ( !LogMan.isInitialized() )
                    {
                        LogMan.initialize( LogMidlet.this );
                    }
                    LogMan.showLogMonitorScreen();
                }
                else if ( c == logViewerCommand )
                {
                    dispRef.setCurrent( new LogViewerScreen( ref ) );
                }
                else if ( c == exitCommand )
                {
                    notifyDestroyed();
                }

            }
        }.start();
    }

    /**
     *
     */
    public void showMainMenu()
    {
        dispRef.setCurrent( frm );
    }
}
