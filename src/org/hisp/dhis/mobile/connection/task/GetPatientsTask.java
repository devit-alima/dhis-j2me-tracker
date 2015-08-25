package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.PatientList;
import org.hisp.dhis.mobile.recordstore.PatientRecordStore;

import com.sun.lwuit.List;

public class GetPatientsTask
    extends AbstractTask
{

    private static final String CLASS_TAG = "GetPatientsTask";

    private String patientIds;

    private NameBasedMIDlet nameBasedMIDlet;

    public GetPatientsTask()
    {
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting GetPatientsTask..." );
        DataInputStream inputStream = null;

        try
        {
            // search patient by id
            inputStream = this.download( patientIds, "patientIds" );
            PatientList patientlist = new PatientList();
            patientlist.deSerialize( inputStream );

            // Save patient to record store
            Vector patients = patientlist.getPatientList();
            for ( int i = 0; i < patients.size(); i++ )
            {
                PatientRecordStore.savePatient( (Patient) patients.elementAt( i ) );
            }

            nameBasedMIDlet.getAlertBoxView( "Instances saved", "Success" ).showView();
        }
        catch ( Exception e )
        {
            nameBasedMIDlet.getAlertBoxView(
                "An error is encountered in downloading instances. Please try again later", "Error" ).showView();
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
    }

    public String getPatientIds()
    {
        return patientIds;
    }

    public void setPatientIds( String patientIds )
    {
        this.patientIds = patientIds;
    }

    public NameBasedMIDlet getNameBasedMIDlet()
    {
        return nameBasedMIDlet;
    }

    public void setNameBasedMIDlet( NameBasedMIDlet nameBasedMIDlet )
    {
        this.nameBasedMIDlet = nameBasedMIDlet;
    }

}
