package org.hisp.dhis.mobile.file;

import java.util.Vector;
import javax.microedition.lcdui.*;

/**
 * 
 * @author Paul Mark Castillo
 */
public class PathsForm
    extends List
    implements CommandListener, OverwriteCallback
{

    /**
     *
     */
    FileManager fileManager;

    /**
     *
     */
    Vector lists;

    /**
     *
     */
    TextField tfSave = new TextField( "Filename: ", "", 50, TextField.ANY );

    /**
     *
     */
    Command saveCommand;

    /**
     *
     */
    Command backCommand = new Command( "Back", Command.BACK, 1 );

    /**
     * 
     * @param fileManager
     * @param lists
     */
    public PathsForm( FileManager fileManager, Vector lists )
    {
        super( "", IMPLICIT );

        String title = (String) fileManager.getPathStack().lastElement();
        title = title.substring( "file:///".length() );
        setTitle( title );

        this.fileManager = fileManager;
        this.lists = lists;

        if ( fileManager.getMode() == FileManager.MODE_SAVE )
        {
            saveCommand = new Command( "Save " + fileManager.getPreferredFilename(), Command.OK, 0 );
            addCommand( saveCommand );
        }

        append( "..", null );
        for ( int i = 0; i < lists.size(); i++ )
        {
            append( (String) lists.elementAt( i ), null );
        }

        addCommand( backCommand );
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
            if ( getSelectedIndex() == 0 )
            {
                fileManager.showPrevious();
            }
            else
            {
                String currentSelected = (String) lists.elementAt( getSelectedIndex() - 1 );
                String newPath = (String) fileManager.getPathStack().lastElement() + currentSelected;

                if ( newPath.endsWith( "/" ) )
                {
                    fileManager.processPaths( newPath, true );
                }
            }
        }
        else if ( c == saveCommand )
        {
            boolean sameFileFound = false;

            for ( int i = 0; i < lists.size(); i++ )
            {
                String currentList = (String) lists.elementAt( i );

                if ( currentList.equals( fileManager.getPreferredFilename() ) )
                {
                    sameFileFound = true;
                }
            }

            if ( sameFileFound )
            {
                Alert alert = new OverwriteFileAlert( this );
                Display.getDisplay( fileManager.getMidlet() ).setCurrent( alert );
            }
            else
            {
                save();
            }
        }
        else if ( c == backCommand )
        {
            fileManager.showPrevious();
        }
    }

    /**
     *
     */
    private void save()
    {
        String currentPath = (String) fileManager.getPathStack().lastElement();
        String completePath = currentPath + fileManager.getPreferredFilename();
        new ProcessFileThread( fileManager, completePath, fileManager.getCallback() ).start();
    }

    /**
     *
     */
    public void okOverwrite()
    {
        save();
    }

    /**
     *
     */
    public void cancelOverwrite()
    {
        Display.getDisplay( fileManager.getMidlet() ).setCurrent( this );
    }
}
