package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;

import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.recordstore.OfflinePatientRecordStore;
import org.hisp.dhis.mobile.recordstore.RelativeRelationshipRecordStore;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class RelativeRegistrationTask
    extends AbstractTask
{
    public static final String CLASS_TAG = "RelativeRegistrationTask";

    private Patient patient;
    
    private Patient relative;

    private NameBasedMIDlet nameBasedMIDlet;

    private String enrollProgramId;

    public RelativeRegistrationTask( Patient patient, String enrollProgramId, Patient relative )
    {
        this.patient = patient;
        this.enrollProgramId = enrollProgramId;
        this.relative = relative;
    }

    public void run()
    {
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();

        Patient patientNew = new Patient();

        try
        {
            DataInputStream inputStream = this.download( SerializationUtil.serialize( this.patient ), enrollProgramId,
                "programid" );

            if ( inputStream != null )
            {
                if ( patient.getId() == 0 )
                {
                    try
                    {
                        OfflinePatientRecordStore.deleteOfflinePatient( patient );
                        RelativeRelationshipRecordStore.deleteAll();
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
            e.printStackTrace();
            if ( e.getMessage().startsWith( "Duplicate value of " ) )
                {
                nameBasedMIDlet.getAlertBoxView( e.getMessage(), "Alert" ).showView();
                nameBasedMIDlet.getPersonDashboardView().setPatient( relative );
                nameBasedMIDlet.getPersonDashboardView().showView();
            }
            else if ( e.getMessage().equalsIgnoreCase( "TCP open" )
                || e.getMessage().equalsIgnoreCase( "Error in HTTP operation" ) )
            {
                ConnectionManager.saveUnregisterdPatient( patient );
                nameBasedMIDlet.getTrackingMainMenuView().showView();

            }
        }

    }

}
