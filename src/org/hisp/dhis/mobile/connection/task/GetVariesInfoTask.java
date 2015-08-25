package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.PatientIdentifierAndAttribute;
import org.hisp.dhis.mobile.recordstore.PatientAttributeRecordStore;
import org.hisp.dhis.mobile.recordstore.PatientIdentifierRecordStore;

public class GetVariesInfoTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "GetVariesInfoTask";
    
    private Vector offlineIdentifierVector;

    private Vector offlineAttributeVector;

    private String programId;

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting GetVariesInfoTask..." );
        
        NameBasedMIDlet nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();

        DataInputStream inputStream = null;

        PatientIdentifierAndAttribute patientIdentifierAndAttribute = new PatientIdentifierAndAttribute();

        try
        {
            inputStream = this.download( programId, "programid" );
            patientIdentifierAndAttribute.deSerialize( inputStream );
            Vector patientIdentifierVector = patientIdentifierAndAttribute.getPatientIdentifiers();

            if ( patientIdentifierVector != null )
            {
                PatientIdentifierRecordStore.savePatientIdentifiers( patientIdentifierVector );
            }

            Vector patientAttributeVector = patientIdentifierAndAttribute.getPatientAttributes();
            if ( patientAttributeVector != null )
            {
                PatientAttributeRecordStore.savePatientAttributes( patientAttributeVector );
            }

            offlineIdentifierVector = PatientIdentifierRecordStore.loadOfflinePatientIdentifiers();
            offlineAttributeVector = PatientAttributeRecordStore.loadOfflinePatientAttribute();

            if ( offlineIdentifierVector.size() == 0 )
            {
                PatientIdentifierRecordStore.saveOfflinePatientIdentifiers( patientIdentifierVector );
            }

            if ( offlineAttributeVector.size() == 0 )
            {
                PatientAttributeRecordStore.saveOffilineAttributes( patientAttributeVector );
            }
            patientIdentifierVector = null;
            patientAttributeVector = null;

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "Network,"+CLASS_TAG, e );
        }
        finally
        {
//            nameBasedMIDlet.getPersonRegistrationView().setEnrollProgramId( programId );
            nameBasedMIDlet.getPersonRegistrationView().showView();
        }

    }

    public Vector getOfflineIdentifierVector()
    {
        return offlineIdentifierVector;
    }

    public void setOfflineIdentifierVector( Vector offlineIdentifierVector )
    {
        this.offlineIdentifierVector = offlineIdentifierVector;
    }

    public Vector getOfflineAttributeVector()
    {
        return offlineAttributeVector;
    }

    public void setOfflineAttributeVector( Vector offlineAttributeVector )
    {
        this.offlineAttributeVector = offlineAttributeVector;
    }

    public String getProgramId()
    {
        return programId;
    }

    public void setProgramId( String programId )
    {
        this.programId = programId;
    }
}
