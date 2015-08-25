package org.hisp.dhis.mobile.connection.task;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;

public class UpdateNewVersionTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "UpdateNewVersionTask";

    public static boolean isDownloadSuccessfully = false;

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting UpdateNewVersionTask..." );
        
        DHISMIDlet.setDownloading( true );
        InputStream inputStream = null;

        String filePath = "file:///";

        try
        {
            HttpConnection hcon = null;
            hcon = ConnectionManager.createConnection();
            int status = hcon.getResponseCode();
            if ( status == HttpConnection.HTTP_OK )
            {
                inputStream = hcon.openInputStream();
            }
            LoginTask.inputStream.close();

            if ( inputStream != null )
            {
                byte buf[] = new byte[1024];

                Enumeration en = FileSystemRegistry.listRoots();

                String root = (String) en.nextElement();
                filePath += root + "DHISMobile-Aggregate.jar";
                FileConnection fc = (FileConnection) Connector.open( filePath, Connector.READ_WRITE );
                if ( !fc.exists() )
                {
                    fc.create();
                }
                OutputStream dos = fc.openOutputStream();
                int len;
                while ( (len = inputStream.read( buf )) > 0 )
                    dos.write( buf, 0, len );
                dos.flush();
                fc.close();
                isDownloadSuccessfully = true;
            }

        }
        catch ( Exception e )
        {
            LogMan.log( "Network," + CLASS_TAG, e );
            e.printStackTrace();
            isDownloadSuccessfully = false;
        }

    }

}
