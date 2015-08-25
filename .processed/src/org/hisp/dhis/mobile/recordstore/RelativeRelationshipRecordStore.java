package org.hisp.dhis.mobile.recordstore;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.log.LogMan;

public class RelativeRelationshipRecordStore
{
    public static final String CLASS_TAG = "RelativeRelationshipRecordStore";

    public static final String RELATIVEID_DB = "RELATIVEID";

    public RelativeRelationshipRecordStore()
    {

    }

    public static void saveId( String id )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException
    {
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( RELATIVEID_DB, true );
            byte[] rec = id.getBytes();
            try
            {
                recordStore.addRecord( rec, 0, rec.length );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

        }
        finally
        {
            System.gc();
        }
    }

    public static String load()
        throws RecordStoreNotOpenException, RecordStoreException
    {
        String subject = null;
        RecordStore rs = null;
        RecordEnumeration re = null;

        try
        {
            rs = RecordStore.openRecordStore( RELATIVEID_DB, true );
            re = rs.enumerateRecords( null, null, false );
            while ( re.hasNextElement() )
            {
                subject = new String( re.nextRecord() );

            }
        }
        finally
        {
            if ( re != null )
                re.destroy();
            if ( rs != null )
                rs.closeRecordStore();
            System.gc();
        }

        return subject;
    }

    public static void deleteAll()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException
    {
        RecordStore rs = RecordStore.openRecordStore( RELATIVEID_DB, true );
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
