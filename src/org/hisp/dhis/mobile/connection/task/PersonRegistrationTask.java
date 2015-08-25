package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;

import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.recordstore.OfflinePatientRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class PersonRegistrationTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "PersonRegistrationTask";

    private Patient patient;

    private NameBasedMIDlet nameBasedMIDlet;

    private String enrollProgramId;

    public PersonRegistrationTask( Patient patient, String enrollProgramId )
    {
        this.patient = patient;
        this.enrollProgramId = enrollProgramId;
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting PersonRegistrationTask..." );
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();

        Patient patientNew = new Patient();

        try
        {
            LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Trying to connect..." );
            DataInputStream inputStream = this.download( SerializationUtil.serialize( this.patient ), enrollProgramId,
                "programid" );

            if ( inputStream != null )
            {
                if ( patient.getId() == 0 )
                {
                    try
                    {
                        OfflinePatientRecordStore.deleteOfflinePatient( patient );
                    }
                    catch ( RecordStoreException e )
                    {
                        e.printStackTrace();
                    }
                }

                patientNew.deSerialize( inputStream );

                nameBasedMIDlet.getDashboardLinkView().setEnrollProgramId( Integer.parseInt( enrollProgramId ) );
                nameBasedMIDlet.getDashboardLinkView().setPatient( patientNew );
                nameBasedMIDlet.getDashboardLinkView().showView();
            }
            inputStream = null;
            patientNew = null;
            System.gc();

        }

        catch ( Exception e )
        {
            LogMan.log( "Network," + CLASS_TAG, e );
            e.printStackTrace();
            if ( e.getMessage().startsWith( "Duplicate value of " ) )
            {
                nameBasedMIDlet.getAlertBoxView( e.getMessage(), "Alert" ).showView();

                try
                {
                    Program program = ProgramRecordStore.getProgram( Integer.parseInt( enrollProgramId ) );
                    patient.getEnrollmentPrograms().removeElementAt( patient.getEnrollmentPrograms().size() - 1 );
                    nameBasedMIDlet.getPersonRegistrationView().setProgram( program );
                    nameBasedMIDlet.getPersonRegistrationView().setPatient( patient );
                    nameBasedMIDlet.getPersonRegistrationView().prepareView();
                    nameBasedMIDlet.getPersonRegistrationView().showView();
                }
                catch ( Exception ex )
                {
                    nameBasedMIDlet.getTrackingMainMenuView().showView();
                }
            }
            else if ( e.getMessage().equalsIgnoreCase( "TCP open" )
                || e.getMessage().equalsIgnoreCase( "Error in HTTP operation" ) )
            {
                ConnectionManager.saveUnregisterdPatient( patient );
                nameBasedMIDlet.getTrackingMainMenuView().showView();

            }
        }

    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

}
