package org.hisp.dhis.mobile.recordstore;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.model.Model;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.recordstore.filter.OfflinePatientFilter;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class OfflinePatientRecordStore
{
    public static final String CLASS_TAG = "OfflinePatientRecordStore";

    public static final String OFFLINEPATIENT_DB = "OFFLINEPATIENT";

    public OfflinePatientRecordStore()
    {
    }

    public static boolean saveOfflinePatientVector( Vector offlinePatientVector )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        for ( int i = 0; i < offlinePatientVector.size(); i++ )
        {
            Patient patient = (Patient) offlinePatientVector.elementAt( i );
            saveOfflinePatient( patient );
        }
        return true;

    }

    public static boolean saveOfflinePatient( Patient patient )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( OFFLINEPATIENT_DB, true );
            OfflinePatientFilter offlinePatientFilter = new OfflinePatientFilter( patient );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( offlinePatientFilter, null, false );
            byte[] bytes = SerializationUtil.serialize( patient );
            if ( recordEnum.numRecords() > 0 )
            {
                int id = recordEnum.nextRecordId();
                recordStore.setRecord( id, bytes, 0, bytes.length );
            }
            else
            {
                recordStore.addRecord( bytes, 0, bytes.length );
            }
            recordEnum.destroy();
            recordStore.closeRecordStore();

        }
        finally
        {
            System.gc();
        }

        return true;

    }

    public static boolean deleteOfflinePatient( Patient patient )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( OFFLINEPATIENT_DB, true );
            OfflinePatientFilter offlinePatientFilter = new OfflinePatientFilter( patient );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( offlinePatientFilter, null, false );

            if ( recordEnum.numRecords() > 0 )
            {
                int id = recordEnum.nextRecordId();
                recordStore.deleteRecord( id );
                return true;
            }

            recordEnum.destroy();
            recordStore.closeRecordStore();

        }
        finally
        {
            System.gc();
        }

        return false;

    }

    public static Vector loadAllOfflinePatients()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Vector offlinePatientVector = null;
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( OFFLINEPATIENT_DB, true );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( null, null, false );
            offlinePatientVector = new Vector();
            while ( recordEnum.hasNextElement() )
            {
                Patient patient = new Patient();
                SerializationUtil.deSerialize( patient, recordEnum.nextRecord() );
                offlinePatientVector.addElement( patient );
            }
            recordEnum.destroy();
            recordStore.closeRecordStore();
        }
        finally
        {
            System.gc();
        }
        return offlinePatientVector;

    }

    public static Vector loadAllOfflinePatientsByTrackedEntity()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Vector offlinePatientVector = null;
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( OFFLINEPATIENT_DB, true );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( null, null, false );
            offlinePatientVector = new Vector();
            while ( recordEnum.hasNextElement() )
            {
                Patient patient = new Patient();
                SerializationUtil.deSerialize( patient, recordEnum.nextRecord() );

                int index;
                for ( index = offlinePatientVector.size(); index > 0; index-- )
                {
                    if ( ((Patient) offlinePatientVector.elementAt( index - 1 )).getTrackedEntityName().equals(
                        patient.getTrackedEntityName() ) )
                    {
                        break;
                    }
                }

                if ( index > 0 )
                {
                    offlinePatientVector.insertElementAt( patient, index - 1 );
                }
                else
                {
                    offlinePatientVector.addElement( patient );
                }
            }
            recordEnum.destroy();
            recordStore.closeRecordStore();
        }
        finally
        {
            System.gc();
        }
        return offlinePatientVector;

    }

    public static void deleteAllOfflinePatient()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException
    {
        RecordStore rs = RecordStore.openRecordStore( OFFLINEPATIENT_DB, true );
        RecordEnumeration re = null;
        try
        {
            re = rs.enumerateRecords( null, null, true );
        }
        catch ( RecordStoreNotOpenException ex )
        {
            LogMan.log( "RMS," + CLASS_TAG, ex );
            ex.printStackTrace();
        }

        int rid = 0;

        try
        {
            while ( re.hasNextElement() )
            {
                rid = re.nextRecordId();
                try
                {
                    rs.deleteRecord( rid );
                }
                catch ( RecordStoreNotOpenException ex )
                {
                    LogMan.log( "RMS," + CLASS_TAG, ex );
                    ex.printStackTrace();
                }
                catch ( InvalidRecordIDException ex )
                {
                    LogMan.log( "RMS," + CLASS_TAG, ex );
                    ex.printStackTrace();
                }
                catch ( RecordStoreException ex )
                {
                    LogMan.log( "RMS," + CLASS_TAG, ex );
                    ex.printStackTrace();
                }
            }
        }
        catch ( InvalidRecordIDException ex )
        {
            LogMan.log( "RMS," + CLASS_TAG, ex );
            ex.printStackTrace();
        }

    }

}
