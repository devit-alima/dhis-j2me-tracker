package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.ui.Text;

public class FindVisitScheduleTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "FindVisitScheduleTask";

    private Program program;

    private String info;

    private NameBasedMIDlet nameBasedMIDlet;

    public FindVisitScheduleTask( Program program, String info )
    {
        this.program = program;
        this.info = info;
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting FindVisitScheduleTask..." );
        DataInputStream inputStream = null;
        try
        {
            inputStream = this.downloadUncompress( info, "details" );
            if ( inputStream != null )
            {
                String message = this.readMessage( inputStream );
                Vector instanceInfos = new Vector();
                while ( message.length() > 0 )
                {
                    instanceInfos.addElement( message.substring( 0, message.indexOf( "$" ) ) );
                    message = message.substring( message.indexOf( "$" ) + 1, message.length() );
                }
                nameBasedMIDlet.getVisitScheduleListView().setInstanceInfos( instanceInfos );
                nameBasedMIDlet.getVisitScheduleListView().setProgram( program );
                nameBasedMIDlet.getVisitScheduleListView().showView();
                instanceInfos = null;
            }
            inputStream = null;
            System.gc();
        }
        catch ( IOException e )
        {
            LogMan.log( CLASS_TAG, e );
            e.printStackTrace();

            String message = null;

            if ( e.getMessage().equalsIgnoreCase( "NO_EVENT_FOUND" ) )
            {
                nameBasedMIDlet.getAlertBoxView( "No result found.", "Alert" ).showView();
                nameBasedMIDlet.getSearchVisitScheduleView().setProgram( program );
                nameBasedMIDlet.getSearchVisitScheduleView().showView();
            }
            else if ( e.getMessage().equalsIgnoreCase( Text.HTTP_ERROR() )
                || e.getMessage().equalsIgnoreCase( "TCP open" ) )
            {
                nameBasedMIDlet.getAlertBoxView( "Internet is not available, Please try again later.", "Alert" )
                    .showView();
                nameBasedMIDlet.getSearchVisitScheduleView().setProgram( program );
                nameBasedMIDlet.getSearchVisitScheduleView().showView();
            }
            else
            {
                message = e.getMessage();
                Vector patientInfos = new Vector();
                while ( message.length() > 0 )
                {
                    patientInfos.addElement( message.substring( 0, message.indexOf( "$" ) ) );
                    message = message.substring( message.indexOf( "$" ) + 1, message.length() );
                }
                nameBasedMIDlet.getPersonListView().setPatientInfos( patientInfos );
                patientInfos = null;
                nameBasedMIDlet.getPersonListView().showView();
            }
        }
    }

}
