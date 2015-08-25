package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.recordstore.PatientRecordStore;

public class GetPatientTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "GetPatientTask";

    private String patientId;

    private NameBasedMIDlet nameBasedMIDlet;

    private Program vsProgram;

    private int previousScreen;

    public GetPatientTask()
    {
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting GetPatientTask..." );
        DataInputStream inputStream = null;

        try
        {
            // search patient by id
            inputStream = this.download( patientId, "patientId" );
            Patient patient = new Patient();
            patient.deSerialize( inputStream );

            // Save patient to record store
            PatientRecordStore.savePatient( patient );

            // show the patient dashboard
            nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
            nameBasedMIDlet.getPersonDashboardView().setVSProgram( vsProgram );
            nameBasedMIDlet.getPersonDashboardView().setPreviousScreen( previousScreen );
            nameBasedMIDlet.getPersonDashboardView().showView();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
    }

    public String getPatientId()
    {
        return patientId;
    }

    public void setPatientId( String patientId )
    {
        this.patientId = patientId;
    }

    public Program getVSProgram()
    {
        return vsProgram;
    }

    public void setVSProgram( Program vsProgram )
    {
        this.vsProgram = vsProgram;
    }

    public NameBasedMIDlet getNameBasedMIDlet()
    {
        return nameBasedMIDlet;
    }

    public void setNameBasedMIDlet( NameBasedMIDlet nameBasedMIDlet )
    {
        this.nameBasedMIDlet = nameBasedMIDlet;
    }

    public int getPreviousScreen()
    {
        return previousScreen;
    }

    public void setPreviousScreen( int previousScreen )
    {
        this.previousScreen = previousScreen;
    }

}
