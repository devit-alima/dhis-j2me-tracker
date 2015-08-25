package org.hisp.dhis.mobile.file;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 * 
 * @author Paul Mark Castillo
 */
public class RootsForm
    extends List
    implements CommandListener
{

    /**
     *
     */
    FileManager fileManager;

    /**
     *
     */
    Vector roots;

    /**
     *
     */
    Command exitCommand = new Command( "Exit", Command.EXIT, 0 );

    /**
     * 
     * @param roots
     */
    public RootsForm( FileManager fileManager, Vector roots )
    {
        super( "Please Select a File System", IMPLICIT );
        this.fileManager = fileManager;
        this.roots = roots;

        for ( int i = 0; i < roots.size(); i++ )
        {
            append( (String) roots.elementAt( i ), null );
        }

        addCommand( exitCommand );
        setCommandListener( this );
    }

    /**
     * 
     * @param c
     * @param d
     */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == SELECT_COMMAND )
        {
            String path = (String) roots.elementAt( getSelectedIndex() );
            fileManager.processPaths( path, true );
        }
        else if ( c == exitCommand )
        {
            fileManager.exit();
        }
    }
}
