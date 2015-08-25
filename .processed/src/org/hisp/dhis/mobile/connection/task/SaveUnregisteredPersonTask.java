package org.hisp.dhis.mobile.connection.task;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.recordstore.OfflinePatientRecordStore;

public class SaveUnregisteredPersonTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "SaveUnregisteredPersonTask";
    private Patient patient;

    private NameBasedMIDlet nameBasedMIDlet;

    public SaveUnregisteredPersonTask( Patient patient )
    {
        this.patient = patient;
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting SaveUnregisteredPersonTask..." );
        
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        try
        {
            OfflinePatientRecordStore.saveOfflinePatient( this.patient );
            nameBasedMIDlet.getWaitingView().showView();
            nameBasedMIDlet.getTrackingMainMenuView().showView();
        }
        catch ( Exception e )
        {
            LogMan.log( "Network," + CLASS_TAG, e );
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
