package org.hisp.dhis.mobile.file;

import javax.microedition.lcdui.*;

/**
 * 
 * @author Paul Mark Castillo
 */
public class OverwriteFileAlert
    extends Alert
    implements CommandListener
{
    /**
     * 
     */
    Command okCommand = new Command( "OK", Command.OK, 0 );

    /**
     * 
     */
    Command cancelCommand = new Command( "Cancel", Command.CANCEL, 1 );

    /**
     * 
     */
    OverwriteCallback callback;

    /**
     * 
     */
    public OverwriteFileAlert( OverwriteCallback callback )
    {
        super( "Overwrite File", "Are you sure you want to overwrite the existing file?", null, AlertType.WARNING );
        this.callback = callback;
        setTimeout( FOREVER );

        addCommand( okCommand );
        addCommand( cancelCommand );
        setCommandListener( this );
    }

    /**
     * 
     * @param c
     * @param d
     */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == okCommand )
        {
            callback.okOverwrite();
        }
        else if ( c == cancelCommand )
        {
            callback.cancelOverwrite();
        }
    }
}
