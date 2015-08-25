package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.io.IOException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class CompleteProgramInstanceTask
    extends AbstractTask
{

    private static final String CLASS_TAG = "CompleteProgramInstanceTask";
    
    private static final String PROGRAM_COMPLETED = "program_completed";

    private NameBasedMIDlet nameBasedMIDlet;

    private ProgramInstance programInstance;

    private Patient patient;

    public CompleteProgramInstanceTask( ProgramInstance programInstance, Patient patient )
    {
        super();
        nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        this.programInstance = programInstance;
        this.patient = patient;
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting CompleteProgramInstanceTask..." );
        try
        {
            DataInputStream messageStream = this.upload( SerializationUtil.serialize( programInstance ) );
            String message = this.readMessage( messageStream );
            if(message.equals( PROGRAM_COMPLETED ))
            {
                patient.getEnrollmentPrograms().removeElement( programInstance );
                patient.getCompletedPrograms().addElement( programInstance );
            }
            nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
            nameBasedMIDlet.getPersonDashboardView().showView();
            messageStream = null;
            System.gc();
        }
        catch ( IOException e )
        {
            LogMan.log( CLASS_TAG, e );
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


