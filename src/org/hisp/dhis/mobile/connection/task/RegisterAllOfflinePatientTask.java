package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.recordstore.OfflinePatientRecordStore;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class RegisterAllOfflinePatientTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "RegisterAllOfflinePatientTask";

    private static final String PATIENT_REGISTERED = "patient_registered";

    private Patient patient;

    private NameBasedMIDlet nameBasedMIDlet;

    public RegisterAllOfflinePatientTask( Patient patient )
    {
        this.patient = patient;
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting RegisterAllOfflinePatientTask..." );
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        try
        {
            DataInputStream messageStream = this.upload( SerializationUtil.serialize( this.patient ), "", "programid" );
            String message = this.readMessage( messageStream );
            if ( message.equalsIgnoreCase( PATIENT_REGISTERED ) )
            {
                OfflinePatientRecordStore.deleteAllOfflinePatient();
                nameBasedMIDlet.getWaitingView().showView();
                nameBasedMIDlet.getTrackingMainMenuView().showView();
            }
            else
            {
            }

        }

        catch ( Exception e )
        {
            LogMan.log( "Network,"+CLASS_TAG, e );
            e.printStackTrace();
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
