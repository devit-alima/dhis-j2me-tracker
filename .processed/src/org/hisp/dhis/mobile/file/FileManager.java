package org.hisp.dhis.mobile.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

/**
 * 
 * @author Paul Mark Castillo
 */
public class FileManager
{
    /**
     *
     */
    public static final int MODE_BROWSE = 1;

    /**
     *
     */
    public static final int MODE_SAVE = 1;

    /**
     *
     */
    public static final int MODE_LOAD = 1;

    /**
     *
     */
    private int mode;

    /**
     *
     */
    MIDlet midlet;

    /**
     *
     */
    private AlertDisplayer alertDisplayer;

    /**
     *
     */
    Displayable previousDisplayable;

    /**
     *
     */
    double versionDbl;

    /**
     *
     */
    String fileSeparator;

    /**
     *
     */
    boolean hasFileAPI;

    /**
     *
     */
    private Vector pathStack = new Vector();

    /**
     *
     */
    private FileConnection fileConnection;

    /**
     *
     */
    private String preferredFilename;

    /**
     *
     */
    private SaveFileCallback callback;

    /**
     *
     */
    public FileManager( MIDlet midlet )
    {
        this.midlet = midlet;
        String versionStr = System.getProperty( "microedition.io.file.FileConnection.version" );

        if ( versionStr == null )
        {
            hasFileAPI = false;
        }
        else
        {
            hasFileAPI = true;
            versionDbl = Double.parseDouble( versionStr );
            fileSeparator = System.getProperty( "file.separator" );
        }

        alertDisplayer = new AlertDisplayer( this );
    }

    /**
     *
     */
    public void browseFiles()
    {
        setMode( MODE_BROWSE );
        accessFiles();

    }

    /**
     * 
     * @param preferredFilename
     * @param os
     */
    public void saveFiles( String preferredFilename, SaveFileCallback callback )
    {
        setMode( MODE_SAVE );
        this.preferredFilename = preferredFilename;
        this.callback = callback;
        accessFiles();
    }

    /**
     * 
     * @param is
     */
    public void loadFiles( InputStream is )
    {
        setMode( MODE_LOAD );
        accessFiles();
    }

    /**
     *
     */
    private void accessFiles()
    {
        if ( hasFileAPI )
        {
            previousDisplayable = Display.getDisplay( midlet ).getCurrent();
            processRoots();
        }
        else
        {
            alertDisplayer.dispError( "FileConnection API is not discovered." );
        }
    }

    /**
     *
     */
    public void processRoots()
    {
        new ProcessRootsThread( this ).start();
    }

    /**
     * 
     * @param path
     */
    public void processPaths( String path, boolean addPathStack )
    {
        new ProcessPathsThread( this, path, addPathStack ).start();
    }

    /**
     * 
     * @param roots
     */
    public void showRoots( Vector roots )
    {
        RootsForm rf = new RootsForm( this, roots );
        Display.getDisplay( getMidlet() ).setCurrent( rf );
    }

    /**
     * 
     * @param lists
     */
    public void showPaths( Vector lists )
    {
        PathsForm pf = new PathsForm( this, lists );
        Display.getDisplay( getMidlet() ).setCurrent( pf );
    }

    /**
     *
     */
    public void showPrevious()
    {
        pathStack.removeElementAt( pathStack.size() - 1 );

        if ( pathStack.isEmpty() )
        {
            processRoots();
        }
        else
        {
            processPaths( (String) pathStack.lastElement(), false );
        }
    }

    /**
     *
     */
    public void exit()
    {
        Display.getDisplay( getMidlet() ).setCurrent( previousDisplayable );
        closeConnection();
    }

    /**
     *
     */
    public void closeConnection()
    {
        if ( fileConnection != null )
        {
            try
            {
                fileConnection.close();
            }
            catch ( IOException ioe )
            {
                alertDisplayer.dispException( ioe, "" );
            }
            finally
            {
                fileConnection = null;
            }
        }
    }

    /**
     * @return the midlet
     */
    public MIDlet getMidlet()
    {
        return midlet;
    }

    /**
     * @param midlet the midlet to set
     */
    public void setMidlet( MIDlet midlet )
    {
        this.midlet = midlet;
    }

    /**
     * @return the pathStack
     */
    public Vector getPathStack()
    {
        return pathStack;
    }

    /**
     * @param pathStack the pathStack to set
     */
    public void setPathStack( Vector pathStack )
    {
        this.pathStack = pathStack;
    }

    /**
     * @return the exceptionDisplayer
     */
    public AlertDisplayer getAlertDisplayer()
    {
        return alertDisplayer;
    }

    /**
     * @param exceptionDisplayer the exceptionDisplayer to set
     */
    public void setAlertDisplayer( AlertDisplayer alertDisplayer )
    {
        this.alertDisplayer = alertDisplayer;
    }

    /**
     * @return the fileConnection
     */
    public FileConnection getFileConnection()
    {
        return fileConnection;
    }

    /**
     * @param fileConnection the fileConnection to set
     */
    public void setFileConnection( FileConnection fileConnection )
    {
        this.fileConnection = fileConnection;
    }

    /**
     * @return the mode
     */
    public int getMode()
    {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode( int mode )
    {
        this.mode = mode;
    }

    /**
     * @return the preferredFilename
     */
    public String getPreferredFilename()
    {
        return preferredFilename;
    }

    /**
     * @param preferredFilename the preferredFilename to set
     */
    public void setPreferredFilename( String preferredFilename )
    {
        this.preferredFilename = preferredFilename;
    }

    /**
     * @return the callback
     */
    public SaveFileCallback getCallback()
    {
        return callback;
    }

    /**
     * @param callback the callback to set
     */
    public void setCallback( SaveFileCallback callback )
    {
        this.callback = callback;
    }
}
