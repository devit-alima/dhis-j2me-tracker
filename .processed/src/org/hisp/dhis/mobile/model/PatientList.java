package org.hisp.dhis.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class PatientList extends Model
{
    private Vector patientList;
    
    public PatientList()
    {
    }
    
    public Vector getPatientList()
    {
        return patientList;
    }
    
    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        if ( patientList != null )
        {
            dataOutputStream.writeInt( patientList.size() );
            for ( int i = 0; i <  patientList.size(); i++){
                ((Patient)patientList.elementAt( i )).serialize( dataOutputStream );
            }
        }else{
            dataOutputStream.writeInt( 0 );
        }
    }
    
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        int temp = 0;
        temp = dataInputStream.readInt();
        if(temp > 0){
            patientList = new Vector();
            for(int i = 0; i < temp; i++){
                Patient patient = new Patient();
                patient.deSerialize( dataInputStream );
                patientList.addElement( patient );
            }
        }
    }
}
