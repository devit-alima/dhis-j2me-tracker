package org.hisp.dhis.mobile.file;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.ConnectionClosedException;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.IllegalModeException;

import org.hisp.dhis.mobile.log.LogMan;

/**
 * 
 * @author Paul Mark Castillo
 */
public class ProcessPathsThread
    extends Thread
{
    private static final String CLASS_TAG = "ProcessPathsThread";

    /**
     *
     */
    FileManager fileManager;

    /**
     *
     */
    String path;

    /**
     *
     */
    AlertDisplayer alertDisplayer;

    /**
     *
     */
    Vector pathStack;

    /*
     *
     */
    Vector lists = new Vector();

    /**
     *
     */
    boolean addPathStack;

    /**
     * 
     * @param fileManager
     */
    public ProcessPathsThread( FileManager fileManager, String path, boolean addPathStack )
    {
        this.fileManager = fileManager;
        this.path = path;
        this.addPathStack = addPathStack;
        alertDisplayer = fileManager.getAlertDisplayer();
        pathStack = fileManager.getPathStack();
    }

    /**
     *
     */
    public void run()
    {
        if ( pathStack.isEmpty() )
        {
            path = "file:///" + path;
        }

        boolean successfulConn;
        if ( fileManager.getFileConnection() == null )
        {
            successfulConn = openConnection( path );
        }
        else
        {
            successfulConn = openConnection( path );
        }
        if ( successfulConn )
        {
            boolean successfulList = list();
            if ( successfulList )
            {
                if ( addPathStack )
                {
                    pathStack.addElement( path );
                }
                fileManager.showPaths( lists );
            }
        }
    }

    /**
     * 
     * @param path
     * @return
     */
    public boolean openConnection( String path )
    {
        try
        {
            fileManager.closeConnection();
            fileManager.setFileConnection( (FileConnection) Connector.open( path ) );
            return true;
        }
        catch ( IllegalArgumentException iae )
        {
            LogMan.log( CLASS_TAG, iae );
            alertDisplayer.dispException( iae, "a parameter is invalid." );
        }
        catch ( ConnectionNotFoundException cnfe )
        {
            LogMan.log( CLASS_TAG, cnfe );
            alertDisplayer.dispException( cnfe,
                "the requested connection cannot be made, or the protocol type does not exist." );
        }
        catch ( IOException ioe )
        {
            LogMan.log( CLASS_TAG, ioe );
            alertDisplayer.dispException( ioe, "some other kind of I/O error occurs." );
        }
        catch ( SecurityException se )
        {
            LogMan.log( CLASS_TAG, se );
            alertDisplayer.dispException( se, "a requested protocol handler is not permitted." );
        }
        catch ( Exception e )
        {
            LogMan.log( CLASS_TAG, e );
            alertDisplayer.dispException( e, "" );
        }
        return false;
    }

    /**
     * 
     * @param path
     * @return
     */
    public boolean setConnection( String path )
    {
        try
        {
            fileManager.getFileConnection().setFileConnection( path );
            return true;
        }
        catch ( NullPointerException npe )
        {
            LogMan.log( CLASS_TAG, npe );
            alertDisplayer.dispException( npe, "fileName is null." );
        }
        catch ( SecurityException se )
        {
            LogMan.log( CLASS_TAG, se );
            alertDisplayer
                .dispException(
                    se,
                    "the security of the application does not have the security access to the specified file or directory as requested in the Connector.open method invocation that originally opened this FileConnection." );
        }
        catch ( IllegalArgumentException iae )
        {
            LogMan.log( CLASS_TAG, iae );
            alertDisplayer.dispException( iae, "fileName contains any path specification or does not yet exist. " );
        }
        catch ( IOException ioe )
        {
            LogMan.log( CLASS_TAG, ioe );
            alertDisplayer
                .dispException(
                    ioe,
                    "the current FileConnection is opened on a file, the connection's target is not accessible, or fileName is an invalid filename for the platform (e.g. contains characters invalid in a filename on the platform)." );
        }
        catch ( ConnectionClosedException cce )
        {
            LogMan.log( CLASS_TAG, cce );
            alertDisplayer.dispException( cce, "the connection is closed." );
        }
        catch ( Exception e )
        {
            LogMan.log( CLASS_TAG, e );
            alertDisplayer.dispException( e, "" );
        }
        return false;
    }

    /**
     * 
     * @param fc
     */
    public boolean list()
    {
        try
        {
            Enumeration enu = fileManager.getFileConnection().list( "*", true );
            while ( enu.hasMoreElements() )
            {
                Object element = (String) enu.nextElement();
                lists.addElement( element );
            }
            return true;
        }
        catch ( IOException ioe )
        {
            LogMan.log( CLASS_TAG, ioe );
            alertDisplayer
                .dispException( ioe,
                    "invoked on a file, the directory does not exist, the directory is not accessible, or an I/O error occurs." );
        }
        catch ( ConnectionClosedException cce )
        {
            LogMan.log( CLASS_TAG, cce );
            alertDisplayer.dispException( cce, "the connection is closed." );
        }
        catch ( SecurityException se )
        {
            LogMan.log( CLASS_TAG, se );
            alertDisplayer.dispException( se,
                "the security of the application does not have read access for the directory." );
        }
        catch ( IllegalModeException ime )
        {
            LogMan.log( CLASS_TAG, ime );
            alertDisplayer
                .dispException(
                    ime,
                    "the application does have read access to the connection's target but has opened the connection in Connector.WRITE mode." );
        }
        catch ( NullPointerException npe )
        {
            LogMan.log( CLASS_TAG, npe );
            alertDisplayer.dispException( npe, "filter is null." );
        }
        catch ( IllegalArgumentException iae )
        {
            LogMan.log( CLASS_TAG, iae );
            alertDisplayer
                .dispException(
                    iae,
                    "filter contains any path specification or is an invalid filename for the platform (e.g. contains characters invalid for a filename on the platform)." );
        }
        catch ( Exception e )
        {
            LogMan.log( CLASS_TAG, e );
            alertDisplayer.dispException( e, "" );
        }
        return false;
    }
}
