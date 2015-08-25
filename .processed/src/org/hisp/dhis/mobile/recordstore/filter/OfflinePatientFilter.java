package org.hisp.dhis.mobile.recordstore.filter;

import javax.microedition.rms.RecordFilter;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class OfflinePatientFilter
    implements RecordFilter
{
    private static final String CLASS_TAG = "OfflinePatientFilter";

    private Patient patient;

    public OfflinePatientFilter( Patient patient )
    {
        this.patient = patient;
    }

    public boolean matches( byte[] suspect )
    {
        if ( patient == null )
        {
            return false;
        }
        else
        {
            try
            {
                Patient suspectPatient = new Patient();

                SerializationUtil.deSerialize( suspectPatient, suspect );
                String patientAttributeString = this.getTrackedEntityString( patient );
                String suspecttAttributeString = this.getTrackedEntityString( suspectPatient );

                return patientAttributeString.equals( suspecttAttributeString );

            }
            catch ( Exception e )
            {
                LogMan.log( "RMS," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }
        return false;
    }

    private String getTrackedEntityString( Patient trackedEntity )
    {
        StringBuffer buffer = new StringBuffer();
        for ( int i = 0; i < trackedEntity.getPatientAttValues().size(); i++ )
        {
            PatientAttribute trackedAttribute = (PatientAttribute) trackedEntity.getPatientAttValues().elementAt( i );
            if ( trackedAttribute.getValue() != null )
            {
                buffer.append( trackedAttribute.getValue() );
            }
        }
        return buffer.toString();
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
